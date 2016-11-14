package jp.ac.chiba_fjb.example.googlescript;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class Syukei  extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.syukei);

        /*
        */

        TableLayout tlayout = (TableLayout) findViewById(R.id.tlayout);
        tlayout.setBackgroundResource(R.drawable.kborder);

        for (int i = 0; i < 30; i++) {
            TableRow tableRow = new TableRow(this);
            for (int j = 0; j < 3; j++) {


                TextView textView = new TextView(this);

                textView.setBackgroundResource(R.drawable.kborder);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
                textView.setGravity(Gravity.CENTER);
                float scale = getResources().getDisplayMetrics().density;
                int s = 5;  // dpを指定
                int ss = (int) (s * scale);
                textView.setPadding(ss, ss, ss, ss);

                textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));


                switch (j) {
                    case 0://個人番号
                        String str = "<u>" + i + "</u>";
                        textView.setText(Html.fromHtml(str));
                        break;
                    case 1://点数
                        textView.setText("60");
                        break;
                    case 2://採点日時
                        textView.setText("11/07 14:44");
                        break;
                }

                tableRow.addView(textView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            }
            tlayout.addView(tableRow, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


        }

    }

}