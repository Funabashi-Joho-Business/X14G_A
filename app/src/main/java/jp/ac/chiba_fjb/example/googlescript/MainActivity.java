package jp.ac.chiba_fjb.example.googlescript;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.services.script.model.Operation;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, dialog_newCreate.OnDialogButtonListener {

    int i =0;
    String textViewValue;
    private Bundle mBundle;
    private String mEditValue;

    private GoogleScript mGoogleScript;
    private LinearLayout layout;
    final String[] SCOPES ={"https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/script.storage",
            "https://www.googleapis.com/auth/spreadsheets"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        ImageView edit =(ImageView)findViewById(R.id.imageView2);
        edit.setOnClickListener(this);
        ImageView copy =(ImageView)findViewById(R.id.imageView3);
        copy.setOnClickListener(this);
        ImageView add =(ImageView)findViewById(R.id.imageView4);
        add.setOnClickListener(this);
        ImageView syukei =(ImageView)findViewById(R.id.imageView5);
        syukei.setOnClickListener(this);
        ImageView saiten =(ImageView)findViewById(R.id.imageView6);
        saiten.setOnClickListener(this);
        TextView text1 = (TextView)findViewById(R.id.textView2);
        text1.setOnClickListener(this);
        text1.setTag("textView");


        }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id==R.id.imageView4){
            dialog_newCreate f = new dialog_newCreate();
            f.setOnDialogButtonListener(this);
            f.show(getSupportFragmentManager(),"");

        }else{
            LinearLayout ll = (LinearLayout)findViewById(R.id.layout1);
            int i, iCount;
            iCount = ll.getChildCount();
            for(i=0; i<iCount; i++){
                View view;
                String s;
                view = ll.getChildAt(i);
                s = v.getClass().getName();
                if(s.endsWith("TextView")==true) {
                    view.setBackgroundResource(R.drawable.border);
                    v.setBackgroundResource(R.drawable.tap);
                }
            }


        }

    }

    @Override
    public void onDialogButton(int value,String editValue) {
        if(value == 0){
            mEditValue = editValue;
           setText(mEditValue);

        }
    }

    //解答追加
    public void setText(String textValue){
        LinearLayout layout = (LinearLayout)findViewById(R.id.layout1);
        TextView textView = new TextView(this);             //インスタンスの生成(引数はActivityのインスタンス)
        textView.setTag(textValue);
        textView.setText(""+textView.getTag());                     //テキストの設定
        int paddingDpt = 30;  // dpを指定
        int paddingDpr = 10;  // dpを指定
        float scale = getResources().getDisplayMetrics().density; //画面のdensityを指定。
        int paddingPxt = (int) (paddingDpt * scale);
        int paddingPxr = (int) (paddingDpr * scale);
        textView.setPadding(paddingPxt,paddingPxr,paddingPxt,paddingPxr);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        textView.setBackgroundResource(R.drawable.border);  //背景色の設定
        layout.addView(textView);
        i++;
        textView.setOnClickListener(this);
        ans(textValue);
    }

    //       Drawable border = getResources().getDrawable(R.drawable.border);
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //必要に応じてアカウントや権限ダイアログの表示
        mGoogleScript.onActivityResult(requestCode,resultCode,data);


    }


    protected void ans(String textValue) {//解答
        mGoogleScript = new GoogleScript(this,SCOPES);
        //送信パラメータ
        List<Object> params = new ArrayList<>();
        params.add(textValue);
        //ID,ファンクション名,結果コールバック
        mGoogleScript.execute("1VpVD7z_Jj7jxz4Jj46qT2beZayJIPhq7RnHn3ydGBPfrUpdilyQHG7h1", "ans2",
                params, new GoogleScript.ScriptListener() {
                    @Override
                    public void onExecuted(GoogleScript script, Operation op) {
                        //                     TextView textView = (TextView) findViewById(R.id.textMessage);

                        if (op == null || op.getError() != null){

                            //                       textView.append("Script結果:エラー\n"+op.getError() != null?op.getError().getMessage():"");
                        }else {
                            //戻ってくる型は、スクリプト側の記述によって変わる
                            ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");
                            String ListA[] = new String[160];
                            //int aaa = ansList.size();
                            int x=0;
                            for(int i=0;i < ansList.size();i++){
                                String a = ansList.get(i).toString();
                                ListA[x] =a.substring(1,2);//解答
                                ListA[x+1] =a.substring(4,a.length()-1);//配点
//                                TextView textView2 = new TextView(GoogleLogin.this);
//                                textView2.setText("解答:" + ListA[x]+"　配点:"+ListA[x+1]+"/");
//                                //textView2.setTag(i);
//                                layout.addView(textView2);
                                x = x+2;
                            }
//                            textView.setText(ListA[0]+ListA[1]+ListA[2]+ListA[3]+ListA[4]+ListA[5]);
//                            for(int i=61;i < ansList.size();i++){
//                                String a = ansList.get(i).toString();
//                                ListA[x] =a;//解答
//                                TextView textView2 = new TextView(GoogleLogin.this);
//                                textView2.setText("平均点:" + ListA[x]+"/");
//                                //textView2.setTag(i);
//                                layout.addView(textView2);
//                                x = x+1;
//                            }


                        }
                    }
                });
    }
}
