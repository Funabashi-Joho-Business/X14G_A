package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import jp.ac.chiba_fjb.example.googlescript.GoogleScript;
import jp.ac.chiba_fjb.example.googlescript.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopFragment extends Fragment implements View.OnClickListener, dialog_newCreate.OnDialogButtonListener,TrashFragment.OnDialogButtonListener, BlankFragment.OnDialogButtonListener {

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
    private Handler mHandler = new Handler();
    private Object savedInstanceState;
    private int select = -1;
    private String textTag;
    private String text;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState1) {
        view = inflater.inflate(R.layout.main, container, false);
        savedInstanceState = savedInstanceState1;
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGoogleScript = new GoogleScript(getActivity(),SCOPES);
    }

    @Override
    public void onStart() {
        super.onStart();
        //GASからテスト一覧を表示
        listOutput();
        ImageView trash = (ImageView) view.findViewById(R.id.trash);
        trash.setOnClickListener(this);
        ImageView edit = (ImageView) view.findViewById(R.id.edit);
        edit.setOnClickListener(this);
        ImageView copy = (ImageView) view.findViewById(R.id.copy);
        copy.setOnClickListener(this);
        ImageView add = (ImageView) view.findViewById(R.id.add);
        add.setOnClickListener(this);
        ImageView syukei = (ImageView) view.findViewById(R.id.syukei);
        syukei.setOnClickListener(this);
        ImageView saiten = (ImageView) view.findViewById(R.id.saiten);
        saiten.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        int id = v.getId();
        if (id == R.id.add) { //dialogフラグメントへ、解答作成
            dialog_newCreate f = new dialog_newCreate();
            f.setOnDialogButtonListener(this);
            f.show(getFragmentManager(), "");
        } else if (id == R.id.syukei&& textViewFlag) { //集計確認
            SyukeiFragment syukeiFragment = new SyukeiFragment();
            bundle.putString("TextView",text);
            syukeiFragment.setArguments(bundle);
            ft.replace(R.id.mainLayout, syukeiFragment, SyukeiFragment.class.getName());
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.edit && textViewFlag) { //解答編集
            KaitouFragment kaitouFragment = new KaitouFragment();   //画面遷移先のフラグメントをインスタンス化
            bundle.putString("TextTag",textTag);                  //put[型名]で("名前",値)　例： putString("num","ないよう");
            bundle.putString("TextView",text);
            kaitouFragment.setArguments(bundle);                    //セット
            ft.replace(R.id.mainLayout, kaitouFragment, KaitouFragment.class.getName());
            ft.addToBackStack(null);
            ft.commit();
        } else if (id == R.id.saiten && textViewFlag) {   //カメラ起動
            bundle.putString("TextTag",textTag);
            bundle.putString("TextView",text);
            CameraFragment camera = new CameraFragment();
            camera.setArguments(bundle);
            ft.replace(R.id.mainLayout,camera, CameraFragment.class.getName());
            ft.addToBackStack(null);
            ft.commit();
            OpenCVLoader.initDebug();
        } else if (v.getId() == R.id.trash && textViewFlag) {
            //フラグメントのインスタンスを作成
            TrashFragment f = new TrashFragment();
            //ダイアログのボタンが押された場合の動作
            f.setOnDialogButtonListener(this);
            f.show(getFragmentManager(), "");
        } else if (v.getClass() == TextView.class) {
            textTag = ((TextView)v).getTag().toString();
            text =  ((TextView)v).getText().toString();
            textViewFlag = true;
            selectText = ((TextView) v).getText().toString();
            LinearLayout ll = (LinearLayout) view.findViewById(R.id.layout1);
            select = ll.indexOfChild(v);
            int i, iCount;
            iCount = ll.getChildCount();
            for (i = 0; i < iCount; i++) {
                View view = ll.getChildAt(i);
                String s = v.getClass().getName();
                if (s.endsWith("TextView") == true) {
                    view.setBackgroundResource(R.drawable.border);
                    v.setBackgroundResource(R.drawable.tap);
                    //System.out.println(selectText.getText().toString());
                    mTextValue = (String) v.getTag();
                }
            }
        }else if (v.getId() == R.id.copy && textViewFlag) {



        }
    }

    @Override
    public void onDialogButton(int value, String editValue) {
        if (value == 0) {
            mEditValue = editValue;
            ansCreate(mEditValue);
        } else {

        }
    }

    //解答追加
    public void setText(String textValue, String gdrive_fileId) {
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout1);
        TextView textView = new TextView(getContext());             //インスタンスの生成(引数はActivityのインスタンス)
        textView.setTag(gdrive_fileId);                      //GoogleDrive上のファイル区別用IDをタグとして設定
        textView.setText("" + textValue);                     //テキストの内容設定
        int paddingDpt = 30;  // dpを指定
        int paddingDpr = 10;  // dpを指定
        float scale = getResources().getDisplayMetrics().density; //画面のdensityを指定。
        int paddingPxt = (int) (paddingDpt * scale);
        int paddingPxr = (int) (paddingDpr * scale);
        textView.setPadding(paddingPxt, paddingPxr, paddingPxt, paddingPxr);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setBackgroundResource(R.drawable.border);  //背景色の設定
        System.out.println("タグ結果:" + textView.getTag());
        layout.addView(textView);
        textView.setOnClickListener(this);

    }

    protected void ansCreate(final String textValue) {
        String s = "初期値";
        final String value = textValue;
        mGoogleScript = new GoogleScript(getActivity(), SCOPES);
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

    public TopFragment() {
        // Required empty public constructor

    }

    public void listOutput() {
        //解答名一覧取得
        mGoogleScript = new GoogleScript(getActivity(), SCOPES);
        textViewFlag = false;
       //強制的にアカウントを切り替える場合
//        mGoogleScript.resetAccount();

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
                                        System.out.println("解答名：" + s.get(i) + "\t解答ID：" + s.get(i + 1));
                                        setText("" + s.get(i), "" + s.get(i + 1));
                                        i += 2;
                                    }
                                }
                            }
                        });
                    }
                });
    }


    @Override
    public void onDialogButton(int value) {
        if (value == 0) {
            LinearLayout ll = (LinearLayout)view.findViewById(R.id.layout1);
            int i, iCount;
            iCount = ll.getChildCount();
            String s ="";
            for (i = 0; i < iCount; i++) {
                if (select != -1) {
                    TextView t = (TextView) ll.getChildAt(select);
                    s=t.getText().toString();
                    ll.removeView(ll.getChildAt(select));

                    select = -1;
                    textViewFlag = false;
                }
            }

            List<Object> params = new ArrayList<>();
            params.add(s);
            //ID,ファンクション名,結果コールバック
            mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "tdelete",
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

                                    }
                                }
                            });
                        }
                    });
        }

    }
}
