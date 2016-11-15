package jp.ac.chiba_fjb.example.googlescript;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.script.model.Operation;

import org.opencv.android.InstallCallbackInterface;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, dialog_newCreate.OnDialogButtonListener {

    private String selectText;
    private String mEditValue;
    private String mTextValue;
    private boolean textViewFlag = false;
    final String[] SCOPES = {
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/script.storage",
            "https://www.googleapis.com/auth/spreadsheets"};

    private GoogleScript mGoogleScript;
    private Handler mHandler;
    private Object savedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        //インスタンスの取得
        savedInstanceState = (Bundle) this.savedInstanceState;
        mHandler = new Handler(); //Android.os

        ImageView edit = (ImageView) findViewById(R.id.edit);
        edit.setOnClickListener(this);
        ImageView copy = (ImageView) findViewById(R.id.copy);
        copy.setOnClickListener(this);
        ImageView add = (ImageView) findViewById(R.id.add);
        add.setOnClickListener(this);
        ImageView syukei = (ImageView) findViewById(R.id.syukei);
        syukei.setOnClickListener(this);
        ImageView saiten = (ImageView) findViewById(R.id.saiten);
        saiten.setOnClickListener(this);

        listOutput();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        int id = v.getId();
        if(id==R.id.add) { //dialogフラグメントへ、解答作成
            dialog_newCreate f = new dialog_newCreate();
            f.setOnDialogButtonListener(this);
            f.show(getSupportFragmentManager(), "");
        }else if(id==R.id.syukei) { //集計確認
            intent.setClassName(this, "jp.ac.chiba_fjb.example.googlescript.Syukei");
            startActivity(intent);
        }else if(id==R.id.edit && textViewFlag){ //解答編集
            intent.setClassName(this, "jp.ac.chiba_fjb.example.googlescript.Kaitou");
            startActivity(intent);
        }else if(id == R.id.saiten && textViewFlag) {   //カメラ起動
            setContentView(R.layout.activity_main);
            TextView answerName = (TextView)findViewById(R.id.AnswerName);
            answerName.setText(selectText);
            if (savedInstanceState == null){
                Fragment f = getSupportFragmentManager().getFragment(new Bundle(),CameraFragment.class.getName());
                if(f == null) {
                    f = new CameraFragment();
                }
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.CameraView
                        ,f,CameraFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
            }
            OpenCVLoader.initDebug();
        }else if(v.getClass()==TextView.class){
            textViewFlag = true;
            selectText = ((TextView)v).getText().toString();
            LinearLayout ll = (LinearLayout) findViewById(R.id.layout1);
            int i, iCount;
            iCount = ll.getChildCount();
            for (i = 0; i < iCount; i++) {
                View view =ll.getChildAt(i);
                String s = v.getClass().getName();
                if (s.endsWith("TextView") == true) {
                    view.setBackgroundResource(R.drawable.border);
                    v.setBackgroundResource(R.drawable.tap);
                    //System.out.println(selectText.getText().toString());
                    mTextValue = (String) v.getTag();
                }
           }
        }
    }

    @Override
    public void onDialogButton(int value,String editValue) {
        if(value == 0){
            mEditValue = editValue;
           ansCreate(mEditValue);
        }else{
            Toast.makeText(this, "作成されませんでした", Toast.LENGTH_LONG).show();
        }
    }

    //解答追加
    public void setText(String textValue,String gdrive_fileId){
        LinearLayout layout = (LinearLayout)findViewById(R.id.layout1);
        TextView textView = new TextView(this);             //インスタンスの生成(引数はActivityのインスタンス)
        textView.setTag(gdrive_fileId);                      //GoogleDrive上のファイル区別用IDをタグとして設定
        textView.setText(""+textValue);                     //テキストの内容設定
        int paddingDpt = 30;  // dpを指定
        int paddingDpr = 10;  // dpを指定
        float scale = getResources().getDisplayMetrics().density; //画面のdensityを指定。
        int paddingPxt = (int) (paddingDpt * scale);
        int paddingPxr = (int) (paddingDpr * scale);
        textView.setPadding(paddingPxt,paddingPxr,paddingPxt,paddingPxr);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setBackgroundResource(R.drawable.border);  //背景色の設定
        System.out.println("タグ結果:"+textView.getTag());
        layout.addView(textView);
        textView.setOnClickListener(this);

    }

    protected void ansCreate (final String textValue){
        String s = "初期値";
        final String value = textValue;

        mGoogleScript = new GoogleScript(this,SCOPES);
        //強制的にアカウントを切り替える場合
        mGoogleScript.resetAccount();
        //送信パラメータ
                List<Object> params = new ArrayList<>();
                params.add(textValue);

        //ID,ファンクション名,結果コールバック
        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "ans2",
                params, new GoogleScript.ScriptListener() {
                    @Override
                    public void onExecuted(GoogleScript script, final Operation op) {
                        //   TextView textView = (TextView) findViewById(R.id.textMessage);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (op == null || op.getError() != null) {
                                    System.out.println("Script:error"); //       textView.append("Script結果:エラー\n");
                                } else {
                                    //戻ってくる型は、スクリプト側の記述によって変わる
                                    Map<String, Object> r = op.getResponse();
                                    String s = (String) r.get("result");
                                    System.out.println("Script:" + s);
                                    setText(value, s);
                                }
                            }
                        });
                    }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleScript.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
    }


    public void onManagerConnected(int status) {
        switch (status) {
            case LoaderCallbackInterface.SUCCESS:
            {
                Log.i("OpenCV", "OpenCV loaded successfully");
                Mat imageMat = new Mat();
            } break;
            default:
            {
                //super.onManagerConnected(status);
            } break;
        }

    }

    public void onPackageInstall(int operation, InstallCallbackInterface callback) {

    }

    public void listOutput(){
        //解答名一覧取得
        mGoogleScript = new GoogleScript(this, SCOPES);
        //強制的にアカウントを切り替える場合
        mGoogleScript.resetAccount();

        //送信パラメータ
        List<Object> params = new ArrayList<>();
        params.add(null);

        //ID,ファンクション名,結果コールバック
        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "init",
                params, new GoogleScript.ScriptListener() {
                    @Override
                    public void onExecuted(GoogleScript script, final Operation op) {
                        //   TextView textView = (TextView) findViewById(R.id.textMessage);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (op == null || op.getError() != null) {
                                    System.out.println("Script:error"); //       textView.append("Script結果:エラー\n");
                                } else {
                                    //戻ってくる型は、スクリプト側の記述によって変わる
                                    Map<String, Object> r = op.getResponse();
                                    ArrayList<Object> s = new ArrayList<Object>();
                                    s = (ArrayList<Object>) r.get("result");
                                    int i = 0;
                                    while (i < s.size()) {
                                        System.out.println("解答名："+s.get(i)+"\t解答ID："+s.get(i+1));
                                        setText(""+s.get(i), ""+s.get(i+1));
                                        i += 2;
                                    }
                                }
                            }
                        });
                    }
                });
    }
    @Override
    public void onBackPressed() {
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCnt != 0) {
            getSupportFragmentManager().popBackStack();
        }
        else
            super.onBackPressed();
    }
}

