package jp.ac.chiba_fjb.example.googlescript.Fragment;

import android.os.Bundle;
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

import jp.ac.chiba_fjb.example.googlescript.R;


public class SyukeiFragment extends Fragment implements View.OnClickListener {

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.syukei, container, false);
 //       requestWindowFeature(Window.FEATURE_NO_TITLE);
        TableLayout tlayout = (TableLayout)v.findViewById(R.id.tlayout);
        tlayout.setBackgroundResource(R.drawable.kborder);

        TextView bangou = (TextView)v.findViewById(R.id.bangou);
        bangou.setOnClickListener(this);

        TextView tensuu = (TextView)v.findViewById(R.id.tensuu);
        tensuu.setOnClickListener(this);

        TextView nitizi = (TextView)v.findViewById(R.id.nitizi);
        nitizi.setOnClickListener(this);

        for (int i = 0; i < 30; i++) {
            TableRow tableRow = new TableRow(getContext());
            for (int j = 0; j < 3; j++) {

                TextView textView = new TextView(getContext());

                if( (i&1)==0 ){
                    textView.setBackgroundResource(R.drawable.kborder);

                }else{
                    textView.setBackgroundResource(R.drawable.border2);

                }


                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                textView.setGravity(Gravity.CENTER);
                float scale = getResources().getDisplayMetrics().density;
                int s = 5;  // dpを指定
                int ss = (int) (s * scale);
                textView.setPadding(ss,ss,ss,ss);



                switch (j) {
                    case 0://個人番号
                        s=110;
                        String str = "<u>14400" + i + "</u>";
                        textView.setText(Html.fromHtml(str));
                        textView.setOnClickListener(this);
                        break;
                    case 1://点数
                        s=90;
                        textView.setText("60");
                        textView.setGravity(Gravity.RIGHT);
                        break;
                    case 2://採点日時
                        s=161;
                        textView.setText("11/07 14:44");
                        break;
                }

                ss = (int) (s * scale);
                textView.setWidth(ss);
                tableRow.addView(textView,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            }
            tlayout.addView(tableRow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        }
    return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bangou) {


        }else if(v.getId() == R.id.tensuu) {

        }else if(v.getId() == R.id.nitizi){

        }else{
            Kozinseiseki f = new Kozinseiseki();
            //ダイアログのボタンが押された場合の動作
            f.show(getFragmentManager(), "");
        }
    }

}
