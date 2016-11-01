package jp.ac.chiba_fjb.example.googlescript;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;import com.example.x14g008.magonote.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    int i =0;



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
           LinearLayout layout = (LinearLayout)findViewById(R.id.layout1);
           TextView textView = new TextView(this);             //インスタンスの生成(引数はActivityのインスタンス)
            textView.setTag("textView"+i);
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



    //       Drawable border = getResources().getDrawable(R.drawable.border);

}
