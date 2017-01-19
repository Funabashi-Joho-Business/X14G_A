package jp.ac.chiba_fjb.example.googlescript;


import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.script.Script;
import com.google.api.services.script.model.ExecutionRequest;
import com.google.api.services.script.model.Operation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GoogleScript
{



	public static interface ScriptListener{
		public void onExecuted(GoogleScript script, Operation op);
	}
	static class ScriptInfo{
		public String scriptId;
		public String functionName;
		public List<Object> params;
		public ScriptListener listener;
	}

	private static final int REQUEST_ACCOUNT_PICKER = 1234;
	private static final int REQUEST_AUTHORIZATION = 1235;
	private static final String EXTRA_NAME = "SCRIPT_INFO";
	private static final String PREF_ACCOUNT_NAME = "ScriptUser";
	private static final String[] SCOPES = {
			"https://www.googleapis.com/auth/drive"};
	private Set<ScriptInfo> mScripts = new HashSet<>();
	private Activity mContext;
	private GoogleAccountCredential mCredential;
	private String mAccountName;
	private Script mService;

	private static HttpRequestInitializer setHttpTimeout(final HttpRequestInitializer requestInitializer) {
		return new HttpRequestInitializer() {
			@Override
			public void initialize(HttpRequest httpRequest)
					throws java.io.IOException {
				requestInitializer.initialize(httpRequest);
				httpRequest.setReadTimeout(380000);
			}
		};
	}

	public GoogleScript(Activity activity, String[] scope) {
		//Activityの保存
		mContext = activity;
		//認証用クラスの生成
		mCredential = GoogleAccountCredential.usingOAuth2(
				activity, Arrays.asList(scope==null?SCOPES:scope))
				.setBackOff(new ExponentialBackOff());
		//登録済みアカウント名を取得
		mAccountName = mContext.getPreferences(Context.MODE_PRIVATE).getString(PREF_ACCOUNT_NAME, null);
	}
	public void resetAccount()	{
		//登録アカウントの解除
		SharedPreferences settings =
				mContext.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(PREF_ACCOUNT_NAME, null);
		editor.apply();
		mAccountName = null;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == REQUEST_ACCOUNT_PICKER) {
			if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
				mAccountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);

				if (mAccountName != null) {
					//アカウント選択確定
					mCredential.setSelectedAccountName(mAccountName);
					SharedPreferences settings =
							mContext.getPreferences(Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString(PREF_ACCOUNT_NAME, mAccountName);
					editor.apply();
				}else{
					requestAccount();
				}

				exec();		//実行要求
			}
			else

				execError();	//実行不能時の処理
		}
		else if(requestCode == REQUEST_AUTHORIZATION) {
			if (resultCode == Activity.RESULT_OK)
				exec();			//実行要求
			else
				execError();	//実行不能時の処理
		}
	}
	public void requestAccount(){
		//ユーザ選択
		mContext.startActivityForResult(mCredential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
	}
	public void execute(String scriptId, String name, List<Object> params, ScriptListener listener){
		//実行に必要な情報を保存
		ScriptInfo info = new ScriptInfo();
		info.scriptId = scriptId;
		info.functionName = name;
		info.params = params;
		info.listener = listener;
		mScripts.add(info);

		if(mAccountName == null){
			//名前選択を呼び出す
			mContext.startActivityForResult(mCredential.newChooseAccountIntent(),REQUEST_ACCOUNT_PICKER);
		}else{
			//名前を確定し実行
			mCredential.setSelectedAccountName(mAccountName);
			exec();
		}
	}
	void execError() {
		//エラーを通知し、実行キューを解除
		for(ScriptInfo info : mScripts) {
			if(info.listener != null)
				info.listener.onExecuted(GoogleScript.this,null);
			mScripts.remove(info);
		}
	}
	void exec(){
		HttpTransport transport = AndroidHttp.newCompatibleTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		mService = new Script.Builder(
				transport, jsonFactory, setHttpTimeout(mCredential))
				.setApplicationName("Google Apps Script Execution API")
				.build();

		//スレッドで実行
		for(final ScriptInfo info : mScripts){
			new Thread() {
				@Override
				public void run() {
					super.run();

					try {
						ExecutionRequest request = new ExecutionRequest().setFunction(info.functionName);
						if(info.params != null)
							request.setParameters(info.params);
						final Operation op = mService.scripts().run(info.scriptId, request).execute();
						mScripts.remove(info);
						if(info.listener != null) {
							mContext.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									info.listener.onExecuted(GoogleScript.this, op);
								}
							});
						}

					} catch (Exception e) {
						if (e instanceof UserRecoverableAuthIOException) {
							//権限要求の呼び出し
							Intent intent = ((UserRecoverableAuthIOException) e).getIntent();
							mContext.startActivityForResult(intent,REQUEST_AUTHORIZATION);
						}
						else if(e instanceof IllegalArgumentException){
							//アカウント要求
							requestAccount();
						}else if(e instanceof GoogleJsonResponseException){
							//サーバー登録ミス
							mScripts.remove(info);
							if(info.listener != null)
								mContext.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										info.listener.onExecuted(GoogleScript.this,null);
									}
								});


						}
						else {
							//登録系エラー
							mScripts.remove(info);
							if(info.listener != null)
								info.listener.onExecuted(GoogleScript.this,null);
							e.printStackTrace();
						}
					}
				}
			}.start();
		}
	}
}