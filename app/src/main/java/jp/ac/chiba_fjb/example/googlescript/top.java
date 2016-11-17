package jp.ac.chiba_fjb.example.googlescript;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.services.script.model.Operation;

import org.opencv.android.OpenCVLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.ac.chiba_fjb.example.googlescript.Camera.CameraFragment;
import jp.ac.chiba_fjb.example.googlescript.Fragment.dialog_newCreate;

/**
 * A simple {@link Fragment} subclass.
 */
public class top extends Fragment implements View.OnClickListener, dialog_newCreate.OnDialogButtonListener {

    private String selectText;
    private String mEditValue;
    private String mTextValue;
    private View view;
    private boolean textViewFlag = false;
    final String[] SCOPES = {
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/script.storage",
            "https://www.googleapis.com/auth/spreadsheets"};

    private GoogleScript mGoogleScript;
    private Handler mHandler;
    private Object savedInstanceState;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState1) {
        view =  inflater.inflate(R.layout.main, container, false);
        savedInstanceState = savedInstanceState1;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mHandler = new Handler(); //Android.os
        //GASからテスト一覧を表示
        //listOutput();
        //解答名一覧取得
        mGoogleScript = new GoogleScript(getActivity(),SCOPES);
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

        ImageView edit = (ImageView)view.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        ImageView copy = (ImageView)view.findViewById(R.id.copy);
        copy.setOnClickListener(this);
        ImageView add = (ImageView)view.findViewById(R.id.add);
        add.setOnClickListener(this);
        ImageView syukei = (ImageView)view.findViewById(R.id.syukei);
        syukei.setOnClickListener(this);
        ImageView saiten = (ImageView)view.findViewById(R.id.saiten);
        saiten.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        int id = v.getId();
        if(id==R.id.add) { //dialogフラグメントへ、解答作成
            dialog_newCreate f = new dialog_newCreate();
            f.setOnDialogButtonListener(this);
            f.show(getFragmentManager(), "");
        }else if(id==R.id.syukei) { //集計確認
            intent.setClassName(getContext(),"jp.ac.chiba_fjb.example.googlescript.Fragment.Syukei");
            startActivity(intent);
        }else if(id==R.id.edit && textViewFlag){ //解答編集
            intent.setClassName(getContext(), "jp.ac.chiba_fjb.example.googlescript.Fragment.Kaitou");
            startActivity(intent);
        }else if(id == R.id.saiten && textViewFlag) {   //カメラ起動
            //(R.layout.activity_main);
            //TextView answerName = (TextView)view.findViewById(R.id.AnswerName);
            //answerName.setText(selectText);
            if (savedInstanceState == null){
                Fragment f = getFragmentManager().getFragment(new Bundle(),CameraFragment.class.getName());
                if(f == null) {
                    f = new CameraFragment();
                }
                ft.replace(R.id.mainLayout,new CameraFragment(),CameraFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
            }
            OpenCVLoader.initDebug();
        }else if(v.getClass()==TextView.class){
            textViewFlag = true;
            selectText = ((TextView)v).getText().toString();
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.layout1);
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

        }
    }

    //解答追加
    public void setText(String textValue,String gdrive_fileId){
        LinearLayout layout = (LinearLayout)view.findViewById(R.id.layout1);
        TextView textView = new TextView(getContext());             //インスタンスの生成(引数はActivityのインスタンス)
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

        mGoogleScript = new GoogleScript(getActivity(),SCOPES);
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
    // BaseFragment.java
    protected void callChildOnActivityResult(FragmentManager fm,
                                             int requestCode, int resultCode, Intent data) {

        List<Fragment> fragments = fm.getFragments();
        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleScript.onActivityResult(requestCode, resultCode, data);
    }

    public top() {
        // Required empty public constructor

    }

    public void listOutput(){
        //解答名一覧取得
        mGoogleScript = new GoogleScript(getActivity(),SCOPES);
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


}
