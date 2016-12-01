package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.common.Scopes;
import com.google.api.services.script.model.Operation;

import java.util.ArrayList;
import java.util.List;

import jp.ac.chiba_fjb.example.googlescript.GoogleScript;
import jp.ac.chiba_fjb.example.googlescript.MainActivity;
import jp.ac.chiba_fjb.example.googlescript.R;

import static jp.ac.chiba_fjb.example.googlescript.R.layout.dialog;

public class KaitouFragment extends Fragment implements View.OnClickListener, BlankFragment.OnDialogButtonListener {

    private GoogleScript mGoogleScript;
    private Handler mHandler = new Handler();
    final String[] SCOPES = {
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/script.storage",
            "https://www.googleapis.com/auth/spreadsheets"};

    private String title;
    private String testId;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.kaitou, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



             Bundle bundle = getArguments();//げっと
            List<Object> params = new ArrayList<>();
            System.out.println(bundle.getString("TextView"));
            mGoogleScript = new GoogleScript(getActivity(), SCOPES);
            params.add(bundle.getString("TextTag"));//指定した名前
            testId = bundle.getString("TextTag");
            title = bundle.getString("TextView");



//        testtitle.setText(kt[0][0]);

        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "ansA",
                params, new GoogleScript.ScriptListener() {
                    public void onExecuted(GoogleScript script, final Operation op) {
                        //   TextView textView = (TextView) findViewById(R.id.textMessage);
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (op == null || op.getError() != null) {
                                    System.out.println("Script:error"); //       textView.append("Script結果:エラー\n");
                                } else {
                                    ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");
                                    drawAnser(ansList);

                                }
                            }
                        });
                    }
                });
    }
    void drawAnser(ArrayList<ArrayList<String>> ansList) {
        View v = getView();

//         テスト用
        final TextView testtitle = (TextView) v.findViewById(R.id.testtitle);

        final String[][] cc = {{"ア", "イ", "ウ", "エ", "オ", "カ", "キ", "ク", "ケ", "コ"}, {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}};
        final String kt[][] = new String[2][80];

        //戻ってくる型は、スクリプト側の記述によって変わる
        for (int i = 0; i < ansList.size()-1; i++) {//正解データを正解と配列に分ける
            String a = ansList.get(i).toString();
            if(a.length()>5){
            kt[0][i] = (a.substring(1, 2));//正解
            kt[1][i] = (a.substring(4, a.length() - 1));
            }else if(a.length()>4){
                kt[0][i] = (a.substring(1, 2));
                kt[1][i] = ""+0;
            }else{
               kt[0][i] = ("");//正解
               kt[1][i] = ""+0;
            }
        }

        testtitle.setText(title);

        LinearLayout layout = (LinearLayout) v.findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.kborder);

        EditText testNo = (EditText) v.findViewById(R.id.testNo);
        testNo.setText("101");
        testNo.setInputType(InputType.TYPE_CLASS_NUMBER);

//        TextView testtitle = (TextView) v.findViewById(R.id.testtitle);
//        testtitle.setText("H27秋_AP午前問題");


        ImageView camera = (ImageView) v.findViewById(R.id.camera);
        camera.setOnClickListener(this);


        ImageView ok = (ImageView) v.findViewById(R.id.editok);
        ok.setOnClickListener(this);
        for (int a = 0; a < 8; a++) {


            TextView sb = new TextView(getContext());
            sb.setGravity(Gravity.CENTER);
            sb.setBackgroundResource(R.drawable.kborder);
            sb.setBackgroundColor(Color.parseColor("#5A5758"));
            sb.setTextColor(Color.parseColor("#EDECEC"));
            sb.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
            sb.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            int pd = 10;
            float scale = getResources().getDisplayMetrics().density;
            int p = (int) (pd * scale);
            sb.setPadding(p, p, p, p);
            sb.setText("" + (a + 1));
            layout.addView(sb);

            for (int i = 0; i < 10; i++) {
                LinearLayout setumon = new LinearLayout(getContext());
                setumon.setOrientation(LinearLayout.HORIZONTAL);
                int m = a * 10 + i;
                setumon.setTag(m);//問題番号のタグ付け

                TextView b = new TextView(getContext());
                b.setText(cc[1][i]);
                b.setGravity(Gravity.CENTER);
                //b.setBackgroundResource(R.drawable.kborder);

                if ((m & 1) == 0) {
                    b.setBackgroundResource(R.drawable.kborder);
                } else {
                    b.setBackgroundResource(R.drawable.border2);

                }

                b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                b.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                int s = 32;  // dpを指定
                int ss = (int) (s * scale);


                b.setWidth(ss);

                setumon.addView(b);

                TableLayout kaitou = new TableLayout(getContext());
                kaitou.setTag(m);

                TableRow tableRow = new TableRow(getContext());
                TableRow tableRow2 = new TableRow(getContext());

                for (int j = 0; j < 10; j++) {
                    TextView text = new TextView(getContext());
                    text.setOnClickListener(this);
                    text.setText(cc[0][j]);
                    if ((m & 1) == 0) {
                        if (text.getText().equals(kt[0][m])) {
                            text.setTag("Ans");//解答にはAnsタグをつける
                            text.setBackgroundResource(R.drawable.btap);//解答は赤く表示する

                        }else {
                            text.setBackgroundResource(R.drawable.kborder);
                        }
                    } else {
                        if (text.getText().equals(kt[0][m])) {
                            text.setTag("Ans");//解答にはAnsタグをつける
                            text.setBackgroundResource(R.drawable.btap);//解答は赤く表示する

                        }else {
                            text.setBackgroundResource(R.drawable.border2);
                        }}
                    ////解答であるかの判定


                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                    text.setGravity(Gravity.CENTER);
                    s = 48;  // dpを指定
                    ss = (int) (s * scale);
                    text.setWidth(ss);
                    text.setHeight(ss);

                    if (j < 5) {
                        tableRow.addView(text);
                    } else {
                        tableRow2.addView(text);
                    }
                }
                kaitou.addView(tableRow, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                kaitou.addView(tableRow2, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                setumon.addView(kaitou, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                EditText haiten = new EditText(getContext());
                haiten.setEms(1);
                haiten.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                haiten.setGravity(Gravity.CENTER);

                haiten.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                haiten.setText(kt[1][m]);
                if ((m & 1) == 0) {
                    haiten.setBackgroundResource(R.drawable.kborder);
                } else {
                    haiten.setBackgroundResource(R.drawable.border2);

                }
                haiten.setTextColor(Color.parseColor("#5A5758"));
                haiten.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

//                haiten.setSelection(1);
                setumon.addView(haiten);

                layout.addView(setumon, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            }
        }
    }
    @Override
    public void onClick(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        String s;
        s = v.getClass().getName();
        if (s.endsWith("TextView") == true) {
            TableLayout tl = (TableLayout) v.getParent().getParent();
            int m = Integer.parseInt(tl.getTag().toString());
            if (v.getTag() != "Ans") {
                View view;
                TableRow tr;
                for (int j = 0; j < 2; j++) {
                    tr = (TableRow) tl.getChildAt(j);
                    for (int i = 0; i < 5; i++) {
                        view = tr.getChildAt(i);
                        if ((m & 1) == 0) {
                            view.setBackgroundResource(R.drawable.kborder);
                        } else {
                            view.setBackgroundResource(R.drawable.border2);

                        }
                        v.setBackgroundResource(R.drawable.btap);
                        v.setTag("Ans");
                    }
                }
            } else {
                if ((m & 1) == 0) {
                    v.setBackgroundResource(R.drawable.kborder);
                } else {
                    v.setBackgroundResource(R.drawable.border2);

                }
                v.setTag("");

            }
        } else if (v.getId() == R.id.editok) {

            ////////////////////////////////////////////////////////////////////////////////////////
            ////解答取得
            ////////////////////////////////////////////////////////////////////////////////////////

            String kaitou[] = new String[163];
            View view = v.getRootView();
            LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout);
            EditText testNo = (EditText)view.findViewById(R.id.testNo);
            String s1;
            View v1;
            LinearLayout setumon;
            TableLayout k;
            TableRow t1, t2;
            TextView t;
            EditText p;

            kaitou[0] = String.valueOf(testNo.getText());
            kaitou[1] = "";

            for (int i = 0; i < layout.getChildCount(); i++) {
                v1 = layout.getChildAt(i);
                s1 = v1.getClass().getName();
                if (s1.endsWith("TextView") == false) {

                    setumon = (LinearLayout) layout.getChildAt(i);
                    k = (TableLayout) setumon.getChildAt(1);
                    t1 = (TableRow) k.getChildAt(0);
                    String a = setumon.getTag().toString();
                    int bangou = Integer.parseInt(a)+1;
                    for (int j = 0; j < 5; j++) {
                        t = (TextView) t1.getChildAt(j);
                        if (t.getTag() == "Ans") {
                            kaitou[bangou*2+1] = t.getText().toString();

                        }
                    }
                    t2 = (TableRow) k.getChildAt(1);
                    for (int j = 0; j < 5; j++) {
                        t = (TextView) t2.getChildAt(j);
                        if (t.getTag() == "Ans") {
                            kaitou[bangou*2+1] =  t.getText().toString();

                        }
                    }

                    if(kaitou[bangou*2+1]==null){
                        kaitou[bangou*2+1] = " ";
                    }
                    p = (EditText) setumon.getChildAt(2);
                    kaitou[bangou*2+2] = p.getText().toString();
                }


            }

            ////////////////////////////////////////////////////////////////////////////////////////
            ////kaitou[0][問題番号]:記号
            ////kaitou[1][問題番号]:配点
            ////問題番号 0～79
            ////////////////////////////////////////////////////////////////////////////////////////



            List<Object> params = new ArrayList<>();
            mGoogleScript = new GoogleScript(getActivity(), SCOPES);
            EditText e = (EditText) view.findViewById(R.id.testtitle);

            params.add(testId);
            System.out.print(testId);
            params.add(String.valueOf(e.getText()));
            params.add(kaitou);

            mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "answ",
                    params, new GoogleScript.ScriptListener() {
                        public void onExecuted(GoogleScript script, final Operation op) {
                            //   TextView textView = (TextView) findViewById(R.id.textMessage);
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (op == null || op.getError() != null) {
                                        System.out.println("Script:error"); //       textView.append("Script結果:エラー\n");
                                    } else {
                                        ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");


                                    }
                                }
                            });
                        }
                    });
            TopFragment topfragment = new TopFragment();
            ft.replace(R.id.mainLayout,topfragment, TopFragment.class.getName());
            ft.addToBackStack(null);
            ft.commit();


        }

    }
    @Override
    public void onDialogButton(int value) {
        new MainActivity().onBackPressed();


    }

}



