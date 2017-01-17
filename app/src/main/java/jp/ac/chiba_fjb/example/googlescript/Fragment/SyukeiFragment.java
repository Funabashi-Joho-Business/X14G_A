package jp.ac.chiba_fjb.example.googlescript.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.api.services.script.model.Operation;

import java.util.ArrayList;
import java.util.List;

import jp.ac.chiba_fjb.example.googlescript.GoogleScript;
import jp.ac.chiba_fjb.example.googlescript.R;


public class SyukeiFragment extends Fragment implements View.OnClickListener {


    String title;
    private GoogleScript mGoogleScript;
    private Handler mHandler = new Handler();
    final String[] SCOPES = {
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/script.storage",
            "https://www.googleapis.com/auth/spreadsheets"};
    private View v;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.syukei, container, false);
        return v;
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        //試験別集計の取得
        Bundle bundle = getArguments();
        List<Object> params = new ArrayList<>();
        mGoogleScript = new GoogleScript(getActivity(), SCOPES);
        title = bundle.getString("TextView");
        params.add(bundle.getString("TextView"));
        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "resA",//
                params, new GoogleScript.ScriptListener() {
                    public void onExecuted(GoogleScript script, final Operation op) {
                        //   TextView textView = (TextView) findViewById(R.id.textMessage);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (op == null || op.getError() != null) {
                                    System.out.println("Script:error"); //       textView.append("Script結果:エラー\n");
                                } else {
                                    ArrayList<ArrayList<Object>> ansList = (ArrayList<ArrayList<Object>>) op.getResponse().get("result");
                                    ArrayList<ArrayList<String>> ansList2 = new ArrayList<ArrayList<String>>();

                                    for(int i = 0;i<ansList.size();i++){
                                        ArrayList<Object> cas = new ArrayList<Object>();
                                        ArrayList<String> scas = new ArrayList<String>();
                                        cas = ansList.get(i);
                                        for(int j = 0;j<cas.size();j++){
                                            String s = cas.get(j).toString();
                                            if (s == null)
                                                s = "";
                                            scas.add(j,s);
                                        }
                                        ansList2.add(i,scas);
                                    }
                                    aggregate(ansList2);

                                }
                            }
                        });
                    }
                });
    }

    void aggregate(ArrayList<ArrayList<String>> resList) {
//        String ListR[] = new String[125];

        ArrayList<String> ListR = new ArrayList<String>();


        //戻ってくる型は、スクリプト側の記述によって変わる
        //（ListR.length -3)/2 -1

        int y = 0;
        for (int i = 0; i < resList.size() - 3; i++) {
            ArrayList<String> tmp = resList.get(i);

            ListR.add(String.valueOf(tmp.get(0)));
            ListR.add(String.valueOf(tmp.get(1)));
            ListR.add(String.valueOf(tmp.get(2)));
//            String a = resList.get(i).toString();
//            if (a.length() > 6) {
//                System.out.print(a);
//                ListR.add(a.substring(1, 7));// 受験者番号
//                if (a.length() > 9) {
//                    ListR.add(a.substring(9, a.length() - 1));
//                } else {
//                    ListR.add("　");
//                }
//            } else {
//
//
//            }
            y = y + 2;
        }

        TextView avg = (TextView)v.findViewById(R.id.textView2);
        TextView max = (TextView)v.findViewById(R.id.textView5);
        TextView min = (TextView)v.findViewById(R.id.textView7);
        ArrayList<String> a = resList.get(60);//平均点
        if(a.get(0).equals("#DIV/0!")){
          a.set(0,"0");
        }
        ListR.add(a.get(0));
        avg.setText(a.get(0));
        a = resList.get(61);//最高点
        ListR.add(a.get(0));
        max.setText(a.get(0));
        a = resList.get(62);//最低点
        ListR.add(a.get(0));
        min.setText(a.get(0));

        //       requestWindowFeature(Window.FEATURE_NO_TITLE);
        TableLayout tlayout = (TableLayout) v.findViewById(R.id.tlayout);
        tlayout.setBackgroundResource(R.drawable.kborder);

        TextView testTitle = (TextView) v.findViewById(R.id.t);
        testTitle.setText(title);

        TextView bangou = (TextView) v.findViewById(R.id.bangou);
        bangou.setOnClickListener(this);

        TextView tensuu = (TextView) v.findViewById(R.id.tensuu);
        tensuu.setOnClickListener(this);

        TextView nitizi = (TextView) v.findViewById(R.id.nitizi);
        nitizi.setOnClickListener(this);


        if(ListR.size() == 0){

        }else{
            for (int i = 0; i < (ListR.size() - 3) / 3; i++) {
                TableRow tableRow = new TableRow(getContext());
                for (int j = 0; j < 3; j++) {

                    TextView textView = new TextView(getContext());

                    if ((i & 1) == 0) {
                        textView.setBackgroundResource(R.drawable.kborder);

                    } else {
                        textView.setBackgroundResource(R.drawable.border2);

                    }


                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    textView.setGravity(Gravity.CENTER);
                    float scale = getResources().getDisplayMetrics().density;
                    int s = 5;  // dpを指定
                    int ss = (int) (s * scale);
                    textView.setPadding(ss, ss, ss, ss);


                    switch (j) {
                        case 0://個人番号
                            s = 110;
                            String str = "<u>" + ListR.get(i * 3) + "</u>";//036
                            System.out.print(ListR.get(i * 2));
                            textView.setText(Html.fromHtml(str));
                            textView.setTag(ListR.get(i * 3));
                            textView.setOnClickListener(this);
                            break;
                        case 1://点数
                            s = 90;
                            textView.setText(ListR.get(i * 3 + 1));//147
                            textView.setGravity(Gravity.RIGHT);
                            break;
                        case 2://採点日時 要確認
                            s = 161;
                            textView.setText(ListR.get(i * 3 + 2));//258
                            break;
                    }

                    ss = (int) (s * scale);
                    textView.setWidth(ss);
                    tableRow.addView(textView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                }
               tlayout.addView(tableRow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            }
        }


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bangou) {


        }else if(v.getId() == R.id.tensuu) {

        }else if(v.getId() == R.id.nitizi){

        }else{
            Kozinseiseki f = new Kozinseiseki();
            Bundle bundle = new Bundle();
            bundle.putString("ID", (String) v.getTag());
            bundle.putString("title",title);
            f.setArguments(bundle);
            //ダイアログのボタンが押された場合の動作

            f.show(getFragmentManager(), "");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mGoogleScript.onActivityResult(requestCode, resultCode, data);
    }
}
