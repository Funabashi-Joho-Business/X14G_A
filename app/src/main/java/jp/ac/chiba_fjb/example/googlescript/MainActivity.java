package jp.ac.chiba_fjb.example.googlescript;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.api.services.script.model.Operation;

import java.util.ArrayList;
import java.util.List;

import static jp.ac.chiba_fjb.example.googlescript.R.layout.activity_main;

/*
//スクリプト側(指定したフォルダを作成する)
function Main(name) {
   DriveApp.getRootFolder().createFolder(name);
   return name+"作成完了";
}
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleScript mGoogleScript;
    Button b2;
    private LinearLayout layout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_main);
        //インスタンスの取得
        layout = (LinearLayout)findViewById(R.id.layout1);


        //キー登録用SHA1の出力(いらなければ消す)
    //    Log.d("フィンガーコード",AppFinger.getSha1(this));
        b2 =(Button)findViewById(R.id.button2);

        //Scriptで必要な権限を記述する
        final String[] SCOPES = {
                "https://www.googleapis.com/auth/drive",
                "https://www.googleapis.com/auth/script.storage",
                "https://www.googleapis.com/auth/spreadsheets"};

        mGoogleScript = new GoogleScript(this,SCOPES);
        //強制的にアカウントを切り替える場合
        // mGoogleScript.resetAccount();



            //送信パラメータ
            List<Object> params = new ArrayList<>();
            params.add("あいうえお");

            //ID,ファンクション名,結果コールバック
            mGoogleScript.execute("1VpVD7z_Jj7jxz4Jj46qT2beZayJIPhq7RnHn3ydGBPfrUpdilyQHG7h1", "action",
                    params, new GoogleScript.ScriptListener() {
                        @Override
                        public void onExecuted(GoogleScript script, Operation op) {
                            TextView textView = (TextView) findViewById(R.id.textMessage);

                            if (op == null || op.getError() != null)
                                textView.append("Script結果:エラー\n" + op.getError() != null ? op.getError().getMessage() : "");
                            else {
                                //戻ってくる型は、スクリプト側の記述によって変わる
                                String s = (String) op.getResponse().get("result");
                                textView.append("Script結果:" + s + "\n");
                                //ans();
                            }
                        }
                    });

        b2.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //必要に応じてアカウントや権限ダイアログの表示
        mGoogleScript.onActivityResult(requestCode,resultCode,data);


    }


    protected void ans() {//解答
        //キー登録用SHA1の出力(いらなければ消す)
        //    Log.d("フィンガーコード",AppFinger.getSha1(this));

        //Scriptで必要な権限を記述する
        final String[] SCOPES = {
                "https://www.googleapis.com/auth/drive",
                "https://www.googleapis.com/auth/script.storage",
                "https://www.googleapis.com/auth/spreadsheets"};

        mGoogleScript = new GoogleScript(this,SCOPES);
        //強制的にアカウントを切り替える場合
         //mGoogleScript.resetAccount();

        //送信パラメータ
        List<Object> params = new ArrayList<>();
        params.add("テスト名");
        //ID,ファンクション名,結果コールバック
        mGoogleScript.execute("1VpVD7z_Jj7jxz4Jj46qT2beZayJIPhq7RnHn3ydGBPfrUpdilyQHG7h1", "Ans",
                params, new GoogleScript.ScriptListener() {
                    @Override
                    public void onExecuted(GoogleScript script, Operation op) {
                        TextView textView = (TextView) findViewById(R.id.textMessage);

                        if(op == null || op.getError() != null)
                            textView.append("Script結果:エラー\n"+op.getError() != null?op.getError().getMessage():"");
                        else {
                            //戻ってくる型は、スクリプト側の記述によって変わる
                            ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");
                            String ListA[] = new String[160];
                            //int aaa = ansList.size();
                            int x=0;
                            for(int i=0;i < ansList.size();i++){
                                String a = ansList.get(i).toString();
                                ListA[x] =a.substring(1,2);//解答
                                ListA[x+1] =a.substring(4,a.length()-1);//配点
                                TextView textView2 = new TextView(MainActivity.this);
                                textView2.setText("解答:" + ListA[x]+"　配点:"+ListA[x+1]+"/");
                                //textView2.setTag(i);
                                layout.addView(textView2);
                                x = x+2;
                            }
//                            textView.setText(ListA[0]+ListA[1]+ListA[2]+ListA[3]+ListA[4]+ListA[5]);
//                            for(int i=61;i < ansList.size();i++){
//                                String a = ansList.get(i).toString();
//                                ListA[x] =a;//解答
//                                TextView textView2 = new TextView(MainActivity.this);
//                                textView2.setText("平均点:" + ListA[x]+"/");
//                                //textView2.setTag(i);
//                                layout.addView(textView2);
//                                x = x+1;
//                            }


                        }
                    }
                });
    }
    protected void res() {//集計一覧
        //キー登録用SHA1の出力(いらなければ消す)
        //    Log.d("フィンガーコード",AppFinger.getSha1(this));

        //Scriptで必要な権限を記述する
        final String[] SCOPES = {
                "https://www.googleapis.com/auth/drive",
                "https://www.googleapis.com/auth/script.storage",
                "https://www.googleapis.com/auth/spreadsheets"};

        mGoogleScript = new GoogleScript(this,SCOPES);
        //強制的にアカウントを切り替える場合
        // mGoogleScript.resetAccount();

        //送信パラメータ
        List<Object> params = new ArrayList<>();
        params.add("テスト名");
        //params.add("個人名");
        //ID,ファンクション名,結果コールバック
        mGoogleScript.execute("1VpVD7z_Jj7jxz4Jj46qT2beZayJIPhq7RnHn3ydGBPfrUpdilyQHG7h1", "Res",
                params, new GoogleScript.ScriptListener() {
                    @Override
                    public void onExecuted(GoogleScript script, Operation op) {
                        TextView textView = (TextView) findViewById(R.id.textMessage);

                        if(op == null || op.getError() != null)
                            textView.append("Script結果:エラー\n"+op.getError() != null?op.getError().getMessage():"");
                        else {
//                            //戻ってくる型は、スクリプト側の記述によって変わる
//                            List<String> res =  (List<String>)op.getResponse().get("result");
//                            int i = 0;
//                            for(String s : res){
//                                TextView textView2 = new TextView(MainActivity.this);
//                                textView2.setTag(s);
//                                textView2.setText("Script結果:"+s+"\n");
//                                layout.addView(textView2);
//                                i++;
//                            }
                            //集計画面表示
                            ArrayList<ArrayList<String>> resList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");
                            String ListR[] = new String[125];
//                            textView.setText("解答:" +resList+"/");

                            int y=0;
                            for(int i=0;i < resList.size()-3;i++){
                                String a = resList.get(i).toString();
                                ListR[y] =a.substring(1,8);//学籍番号
                                ListR[y+1] =a.substring(9,a.length()-1);//点数
                                TextView textView2 = new TextView(MainActivity.this);
                                textView2.setText("学籍番号:" + ListR[y]+"　点数:"+ListR[y+1]+"/");
                                //textView2.setTag(i);
                                layout.addView(textView2);
                                y = y+2;
                            }
//                            y=120;
                            String a = resList.get(60).toString();//平均点
                            ListR[y] = a.substring(1,a.length()-1);
                            a = resList.get(61).toString();//最高点
                            ListR[y+1] = a.substring(1,a.length()-1);
                            a = resList.get(62).toString();//最低点
                            ListR[y+2] = a.substring(1,a.length()-1);
                            TextView textView2 = new TextView(MainActivity.this);
                            textView2.setText("平均点:" + ListR[y]+"最高点:"+ListR[y+1]+"最低点:"+ListR[y+2]+"/");
                            //textView2.setTag(i);
                            layout.addView(textView2);
                        }
                    }
                });
    }

    protected void resP() {//個人集計
        //キー登録用SHA1の出力(いらなければ消す)
        //    Log.d("フィンガーコード",AppFinger.getSha1(this));

        //Scriptで必要な権限を記述する
        final String[] SCOPES = {
                "https://www.googleapis.com/auth/drive",
                "https://www.googleapis.com/auth/script.storage",
                "https://www.googleapis.com/auth/spreadsheets"};

        mGoogleScript = new GoogleScript(this,SCOPES);
        //強制的にアカウントを切り替える場合
        // mGoogleScript.resetAccount();

        //送信パラメータ
        List<Object> params = new ArrayList<>();
        params.add("テスト名");
        params.add("144999");
        //params.add("個人名");
        //ID,ファンクション名,結果コールバック
        mGoogleScript.execute("1VpVD7z_Jj7jxz4Jj46qT2beZayJIPhq7RnHn3ydGBPfrUpdilyQHG7h1", "ResP",
                params, new GoogleScript.ScriptListener() {
                    @Override
                    public void onExecuted(GoogleScript script, Operation op) {
                        TextView textView = (TextView) findViewById(R.id.textMessage);

                        if(op == null || op.getError() != null)
                            textView.append("Script結果:エラー\n"+op.getError() != null?op.getError().getMessage():"");
                        else {
//                            //戻ってくる型は、スクリプト側の記述によって変わる
//                            List<String> res =  (List<String>)op.getResponse().get("result");
//                            int i = 0;
//                            for(String s : res){
//                                TextView textView2 = new TextView(MainActivity.this);
//                                textView2.setTag(s);
//                                textView2.setText("Script結果:"+s+"\n");
//                                layout.addView(textView2);
//                                i++;
//                            }
                            //集計画面表示
                            ArrayList<ArrayList<String>> resList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");
                            String ListRp[] = new String[242];
//                            textView.setText("解答:" +resList+"/");

                            int y=0;
                            for(int i=0;i < resList.size()-2;i++){
                                String a = resList.get(i).toString();
                                ListRp[y] =a.substring(1,2);//正答
                                ListRp[y+1] =a.substring(4,5);//解答
                                ListRp[y+2] =a.substring(6,a.length()-1);//正答率
                                TextView textView2 = new TextView(MainActivity.this);
                                textView2.setText("正答:" + ListRp[y]+"　解答:"+ListRp[y+1]+"　正答率:"+ListRp[y+2]+"/");
                                //textView2.setTag(i);
                                layout.addView(textView2);
                                y = y+3;
                            }
//                            y=240;
                            String a = resList.get(80).toString();//学籍番号
                            ListRp[y] = a.substring(1,a.length()-1);
                            a = resList.get(81).toString();//点数
                            ListRp[y+1] = a.substring(1,a.length()-1);
                            TextView textView2 = new TextView(MainActivity.this);
                            textView2.setText("学籍番号:" + ListRp[y]+"点数:"+ListRp[y+1]+"/");
                            //textView2.setTag(i);
                            layout.addView(textView2);

                            textView.setText("学籍番号:" + ListRp[240]+"点数:"+ListRp[241]+"/");
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        resP();

    }
}



