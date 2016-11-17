package jp.ac.chiba_fjb.example.googlescript.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import jp.ac.chiba_fjb.example.googlescript.MainActivity;
import jp.ac.chiba_fjb.example.googlescript.R;

public class KaitouFragment extends Fragment implements View.OnClickListener, BlankFragment.OnDialogButtonListener {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.kaitou, container, false);

        String[][] cc = {{"ア", "イ", "ウ", "エ", "オ", "カ", "キ", "ク", "ケ", "コ"}, {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j"}};


        LinearLayout layout = (LinearLayout)v.findViewById(R.id.layout);
        layout.setBackgroundResource(R.drawable.kborder);

        TextView testtitle = (TextView)v.findViewById(R.id.testtitle);
        testtitle.setText("テスト名");

        ImageView camera = (ImageView)v.findViewById(R.id.camera);
        camera.setOnClickListener(this);

        ImageView ok = (ImageView)v.findViewById(R.id.editok);
        ok.setOnClickListener(this);

        for (int a = 0; a < 8; a++) {

            LinearLayout toisetumon = new LinearLayout(getContext());
            toisetumon.setBackgroundResource(R.drawable.kborder);


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

                TextView b = new TextView(getContext());
                b.setText(cc[1][i]);
                b.setGravity(Gravity.CENTER);
                b.setBackgroundResource(R.drawable.kborder);
                b.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
                b.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1));
                int s = 32;  // dpを指定
                int ss = (int) (s * scale);
                b.setWidth(ss);

                setumon.addView(b);

                TableLayout kaitou = new TableLayout(getContext());
                kaitou.setTag("" + a + i);

                TableRow tableRow = new TableRow(getContext());
                TableRow tableRow2 = new TableRow(getContext());

                for (int j = 0; j < 10; j++) {

                    TextView text = new TextView(getContext());
                    text.setOnClickListener(this);
                    text.setTag("" + a + i);
                    text.setText(cc[0][j]);
                    text.setBackgroundResource(R.drawable.kborder);
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
                haiten.setText("1");
                haiten.setBackgroundResource(R.drawable.kborder);
                haiten.setTextColor(Color.parseColor("#5A5758"));
                haiten.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                setumon.addView(haiten);

                layout.addView(setumon, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            }
            layout.addView(toisetumon, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    return v;

    }

    @Override
    public void onClick(View v) {
        String s;
        s = v.getClass().getName();
        if (s.endsWith("TextView") == true) {
            TableLayout tl = (TableLayout) v.getParent().getParent();
            View view;
            TableRow tr;
            for (int j = 0; j < 2; j++) {
                tr = (TableRow) tl.getChildAt(j);
                for (int i = 0; i < 5; i++) {
                    view = tr.getChildAt(i);
                    view.setBackgroundResource(R.drawable.kborder);
                    v.setBackgroundResource(R.drawable.btap);
                }
            }
        } else if (v.getId() == R.id.editok) {
            //フラグメントのインスタンスを作成
            BlankFragment f = new BlankFragment();
            //ダイアログのボタンが押された場合の動作
            f.setOnDialogButtonListener(this);
            f.show(f.getFragmentManager(),"");
        }


    }

    @Override
    public void onDialogButton(int value) {
        new MainActivity().onBackPressed();
    }
}