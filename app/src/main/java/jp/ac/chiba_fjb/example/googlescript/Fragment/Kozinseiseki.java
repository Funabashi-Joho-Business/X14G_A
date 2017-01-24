package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.api.services.script.model.Operation;

import java.util.ArrayList;
import java.util.List;

import jp.ac.chiba_fjb.example.googlescript.GoogleScript;
import jp.ac.chiba_fjb.example.googlescript.R;

import static jp.ac.chiba_fjb.example.googlescript.R.layout.kozinseiseki;


/**
 * A simple {@link Fragment} subclass.
 */
public class Kozinseiseki extends DialogFragment implements View.OnClickListener {

    private GoogleScript mGoogleScript;
    private Handler mHandler = new Handler();
    final String[] SCOPES = {
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/script.storage",
            "https://www.googleapis.com/auth/spreadsheets"};
    TableLayout tlayout;
    TextView kid,ktensu;
    private View v;




    public Kozinseiseki() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(kozinseiseki, container, false);
        tlayout = (TableLayout)view.findViewById(R.id.tlayout);
        tlayout.setBackgroundResource(R.drawable.kborder);
        ImageView kettei = (ImageView)view.findViewById(R.id.imageView5);
        kid = (TextView)view.findViewById(R.id.kid);
        ktensu = (TextView)view.findViewById(R.id.ktensu);
        kettei.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle bundle = getArguments();//げっと
        List<Object> params = new ArrayList<>();
        mGoogleScript = new GoogleScript(getActivity(), SCOPES);
        params.add(bundle.getString("title"));
        params.add(bundle.getString("ID"));

        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "ResP",
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
                                    kojin(ansList);

                                }
                            }
                        });
                    }
                });
    }

    public void kojin(ArrayList<ArrayList<String>> ansList){
        String[] cc = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"};
        float scale = getResources().getDisplayMetrics().density;
//  float scale = 500;
        int s=0;
        int ss;

        String id;
        String tensu;

        ArrayList<String> k = new ArrayList<String>();
        String kt[][] = new String[2][80];
        ArrayList<String> a = new ArrayList<String>();
        for (int i = 0; i < 80; i++) {//正解データを正解と配列に分ける
            a = ansList.get(i);
            kt[0][i] = a.get(0);
            kt[1][i] = a.get(1);


//            if(a.length()>4){
//                kt[0][i]= a.substring(1, 2);//正解
//                kt[1][i]= a.substring(3, 4);//ユーザーの解答
//            }else if(a.length()>3){
//                kt[0][i] = a.substring(1,2);
//                kt[1][i] = "";
//            }else{
//                kt[0][i] = "";
//                kt[1][i] = "";
//            }
        }
        //ID 点数　取得処理
//        id = ansList.get(80).toString().substring(1,ansList.get(80).toString().length()-1);
//        tensu =ansList.get(81).toString().substring(1,ansList.get(81).toString().length()-1);
        a = ansList.get(80);
        id = a.get(0);
        a = ansList.get(81);

        tensu = a.get(0).toString();

        kid.setText(id);
        ktensu.setText(tensu);



        for(int t=1;t<9;t++) {
            TextView bangou = new TextView(getActivity());
            bangou.setBackgroundResource(R.drawable.kborder);
            bangou.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22);
            bangou.setGravity(Gravity.CENTER);
            bangou.setText(""+t);
            bangou.setBackgroundColor(Color.parseColor("#5A5758"));
            bangou.setTextColor(Color.parseColor("#EDECEC"));
            tlayout.addView(bangou);

            for (int i = 0; i < 10; i++) {
                TableRow tableRow = new TableRow(getActivity());
                for (int j = 0; j < 3; j++) {

                    TextView textView = new TextView(getActivity());

                    if( (i&1)==0 ){
                        textView.setBackgroundResource(R.drawable.kborder);

                    }else{
                        textView.setBackgroundResource(R.drawable.border2);

                    }
                    switch (j) {
                        case 0://問題番号
                            s = 66;
                            textView.setText("" + cc[i]);
                            //正解のときに色を変える
                            //textView.setBackgroundResource(R.drawable.btap);
                            break;
                        case 1://正解
                            s = 66;
                            textView.setText(kt[0][(t-1)*10+i]);
                            //正解のときに色を変える
                            if(!kt[0][(t-1)*10+i].equals("")&&kt[0][(t-1)*10+i].equals(kt[1][(t-1)*10+i])) {
                                textView.setBackgroundResource(R.drawable.btap);
                            }
                            break;
                        case 2://解答
                            s = 66;
                            textView.setText(kt[1][(t-1)*10+i]);
                            if(!kt[0][(t-1)*10+i].equals("")&&kt[0][(t-1)*10+i].equals(kt[1][(t-1)*10+i])) {
                                textView.setBackgroundResource(R.drawable.btap);
                            }
                            break;
//                        case 3://正答率
//                            s = 90;
//                            textView.setText("10.0%");
//                            break;
                    }

                    if(j==1 || j==2) {
                        ss = (int) (s * scale);
                        textView.setWidth(ss + 75);
                    }else{
                        ss = (int) (s * scale);
                        textView.setWidth(ss + 30);
                    }

                    textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                    textView.setGravity(Gravity.CENTER);
                    ss = (int) (5 * scale);
                    textView.setPadding(ss, ss, ss, ss);


                    tableRow.addView(textView);

                }
                tlayout.addView(tableRow);

            }
        }

    }

    @Override
    public void onClick(View v) {
        //リスナーに登録されているメソッドを呼び出す
        if(mListener != null) {
            if (v.getId() == R.id.dialogyes)
                mListener.onDialogButton(0);
            else
                mListener.onDialogButton(1);
        }
        //ダイアログを閉じる
        getDialog().cancel();
    }

    public interface OnDialogButtonListener{
        void onDialogButton(int value);
    }
    //インタフェイスのインスタンス保存用
    OnDialogButtonListener mListener;

    //ボタン動作のインスタンスを受け取る
    public void setOnDialogButtonListener(OnDialogButtonListener listener){
        mListener =  listener;
    }

}



