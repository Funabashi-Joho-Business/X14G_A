package jp.ac.chiba_fjb.example.googlescript.Camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.api.services.script.model.Operation;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jp.ac.chiba_fjb.example.googlescript.Fragment.SyukeiFragment;
import jp.ac.chiba_fjb.example.googlescript.GoogleScript;
import jp.ac.chiba_fjb.example.googlescript.R;

import static jp.ac.chiba_fjb.example.googlescript.R.id.imageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements CameraPreview.SaveListener, SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private boolean mActive;
    private CameraPreview mCamera;
    private SeekBar mSeekBar;
    private SaveListener mSaveListener;
    private int mVisibility;
    private boolean mStopFlag;
    SimpleDateFormat fmt = new SimpleDateFormat("yyyy/mm/dd hh:mm");

    private ArrayList<ArrayList<Object>> allSend = new ArrayList<ArrayList<Object>>();//個人別集計送信配列
    private ArrayList<Object> testCor = new ArrayList<Object>();//テスト毎集計
    private ArrayList<Double> cornum = new ArrayList<Double>();//正解の配点
    private ArrayList<String> corstr= new ArrayList<String>();//正解の記号

    private String mcmsg = "";
    private String logmsg = "";
    private String title;

    private String tempNo;
    private Double tempPoint;
    private String tempDate;



    //簡易ログのために追加
    final String[] SCOPES = {
            "https://www.googleapis.com/auth/drive",
            "https://www.googleapis.com/auth/script.storage",
            "https://www.googleapis.com/auth/spreadsheets"};

    private GoogleScript mGoogleScript;

    private Handler mHandler = new Handler();
    private View v;
    private Bundle bundle;
    //追加ここまで


    public void CorrectAnser(){//正解データの受け取り
        Bundle bundle = getArguments();
        List<Object> params = new ArrayList<>();
        params.add(bundle.getString("TextTag"));

        mGoogleScript = new GoogleScript(getActivity(),SCOPES);
        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "ansA",
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
                                    ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");

                                    corstr = new ArrayList<String>();
                                    cornum = new ArrayList<Double>();
                                    for(int i = 0;i<ansList.size()-1;i++){//正解データを正解と配列に分ける
                                        String a = ansList.get(i).toString();
                                        System.out.println(a.substring(1,2));
                                        corstr.add(a.substring(1,2));//正解

                                        cornum.add(Double.valueOf(a.substring(3,a.length()-1)));
                                    }
                                }
                            }
                        });
                    }
                });
    }


    public void Agg(ArrayList<String>anser){//集計処理

        Double point = 0.0;
        Date date = new Date();

        ArrayList<Object> send = new ArrayList<Object>();//個人別集計

        if(testCor == null)
            testCor.add(anser.get(1));//試験番号


        send.add(anser.get(0));
        send.add(anser.get(1));
        send.add(fmt.format(date));
         for(int i = 0;i<corstr.size();i++){

            if (corstr.get(i) != null) {
                if(corstr.get(i).equals(anser.get(i+2))){
                    point = cornum.get(i);
                }
                send.add(corstr.get(i));
                send.add(anser.get(i+2));
            }else{
                send.add("　");
                send.add("　");
            }
        }
        send.add(point);
        //個人集計格納配列に入れる
        allSend.add(send);

        TextView log = (TextView)v.findViewById(R.id.log);

        //簡易ログ用データ
        tempDate = date.toString();
        tempNo = anser.get(0);
        tempPoint = point;

        log.setText("読み取り済\n"+mcmsg+"\n"+tempDate+"\n"+tempNo+"\n"+tempPoint);


        //試験別集計データ
        testCor.add(anser.get(0));//受験者番号
        testCor.add(fmt.format(date));//採点日時
        testCor.add(point);//点数

    }

    //GASへの解答の送信処理
    public  void sendGas(ArrayList<Object> testCor, ArrayList<ArrayList<Object>> allSend){

        bundle = getArguments();
        System.out.println(bundle.getString("TextView"));
        List<Object> params = new ArrayList<>();
        params.add(bundle.getString("TextView"));//テスト名
        params.add(testCor);//試験別集計
        params.add(allSend);//個人集計群
        mGoogleScript = new GoogleScript(getActivity(),SCOPES);

        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "getdata",
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
                                    ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");

                                }
                            }
                        });
                    }
                });

    }

    public void sendAns(ArrayList<String> ans){
        Bundle bundle = getArguments();
        List<Object> params = new ArrayList<>();
        params.add(bundle.getString("ans"));
        mGoogleScript = new GoogleScript(getActivity(),SCOPES);

        mGoogleScript.execute("1R--oj7xaQwzKf0Lk33pHyCh8hSGLG85nqUVQDVwM1TYrMqq61jWCEQro", "amsw",
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
                                    ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");

                                }
                            }
                        });
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.saveBtn:
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if(allSend!=null&testCor!=null){
                                sendGas(testCor,allSend);
                            }
                        }
                    });
                thread.start();
                //画面切り替え
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                SyukeiFragment syukeiFragment = new SyukeiFragment();
                syukeiFragment.setArguments(bundle);
                ft.replace(R.id.mainLayout, syukeiFragment, SyukeiFragment.class.getName());
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.light:
                ImageView light = (ImageView)v.findViewById(R.id.light);
                mCamera.setLight(!mCamera.isLight());
                if(mCamera.isLight()){
                    light.setImageResource(R.drawable.lightbutton);
                }else if(!mCamera.isLight()){
                    light.setImageResource(R.drawable.lightbutton2);
                }
                break;
        }

    }


    static interface SaveListener{
        public void onSave(Bitmap bitmap);
    }
    void setOnSaveListener(SaveListener l){
        mSaveListener = l;
    }

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         v =  inflater.inflate(R.layout.fragment_camera, container, false);
        mSeekBar = (SeekBar)v.findViewById(R.id.seekBar);
        mSeekBar.setProgress(110);
        mSeekBar.setOnSeekBarChangeListener(this);

        TextView textView = (TextView) v.findViewById(R.id.textView);
        textView.setText(""+110);

        v.findViewById(R.id.saveBtn).setOnClickListener(this);
        v.findViewById(R.id.light).setOnClickListener(this);

        return v;
    }



    @Override
    public void onStart() {
        super.onStart();
        Thread thread =new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(title);
                CorrectAnser();
            }
        });
        thread.start();
        mCamera = new CameraPreview();
        mCamera.setSaveListener(this);
        TextureView textureView = (TextureView)getView().findViewById(R.id.textureView);
        mCamera.setTextureView(textureView);
        mCamera.open(0);

/*
        //サイズ限定
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point real = getRealSize(getActivity());
        float aspect;

        Configuration config = getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
            aspect = (float)real.y/real.x;
        else
            aspect = (float)real.x/real.y;


        mCamera.setPreviewSize(1280,(int)(1280/aspect));
  */

        //最大サイズ
        mCamera.setPreviewSize();
        mCamera.startPreview();
       // mCamera.save();

    }
    @SuppressLint("NewApi")
    public static Point getRealSize(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point(0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Android 4.2~
            display.getRealSize(point);
            return point;

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            // Android 3.2~
            try {
                Method getRawWidth = Display.class.getMethod("getRawWidth");
                Method getRawHeight = Display.class.getMethod("getRawHeight");
                int width = (Integer) getRawWidth.invoke(display);
                int height = (Integer) getRawHeight.invoke(display);
                point.set(width, height);
                return point;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return point;
    }
    @Override
    public void onStop() {
        mCamera.close();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        mStopFlag = false;
        //フレームスタイルの変更
        View view = getActivity().getWindow().getDecorView();
        mVisibility = view.getSystemUiVisibility();
        view.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

       // mCamera.setLight(true);
    }

    @Override
    public void onPause() {
        mStopFlag = true;
        //フレームスタイルの復元
        View view = getActivity().getWindow().getDecorView();
        view.setSystemUiVisibility(mVisibility );

        mCamera.setLight(false);

        if(mThread != null) {
            while (mThread.isAlive()) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onPause();
    }

    private Thread mThread;
    @Override
    public void onSave(final Bitmap bitmap) {
        if(mStopFlag || bitmap == null)
            return;
        if(mThread != null && mThread.isAlive())
            return;

        final int limit = mSeekBar.getProgress();

        mThread = new Thread(){
            @Override
            public void run() {

                final MarkReader.MarkerInfo info = MarkReader.getMarker(bitmap,limit);
                final MarkReader.AnserData anserData = MarkReader.getAnser(bitmap,info,limit);
                bitmap.recycle();
                mcmsg = String.format("検出マーク数: %d", info.markerCount);

                if(info.markerCount < 4 || anserData==null){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getView()!=null){
                                TextView textView = (TextView) getView().findViewById(R.id  .textView2);
                                TextView log = (TextView)v.findViewById(R.id.log);

                                if(textView != null) {
                                    textView.setText("");

                                    logmsg = ("読み取り中\n"+String.format("検出マーク数: %d", info.markerCount));
                                    log.setText(logmsg);

                                }
                            }
                           }
                    });
                }
                else
                {


                    getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        onReaded(info,anserData);

                                                    }
                                                });

                }

            }
        };
        mThread.start();


    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        TextView textView = (TextView) getView().findViewById(R.id.textView);
        textView.setText(""+progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
    public void onReaded(MarkReader.MarkerInfo info,MarkReader.AnserData anserData){


        StringBuilder sb = new StringBuilder();
        sb.append(String.format("用紙角度 %f\n誤差角度 %f\n(%04d,%04d) %f\n(%04d,%04d) %f\n(%04d,%04d) %f\n(%04d,%04d) %f\n",
                info.angle,90-info.markerAngle[1],
                info.markerPoint[0].x,info.markerPoint[0].y, info.markerAngle[0],
                info.markerPoint[1].x,info.markerPoint[1].y, info.markerAngle[1],
                info.markerPoint[2].x,info.markerPoint[2].y, info.markerAngle[2],
                info.markerPoint[3].x,info.markerPoint[3].y, info.markerAngle[3]));

        //認識したエリアを表示
        PointView pointView = (PointView)getView().findViewById(R.id.pointView);
        pointView.clear();
        pointView.setPoint(Arrays.asList(info.markerPoint),0x30ff0000);


        List<PointF> leftArea = new ArrayList<PointF>();
        leftArea.add(new PointF(1.82f,9.1f));
        leftArea.add(new PointF(1.82f,27.0f));
        leftArea.add(new PointF(9.0f,27.0f));
        leftArea.add(new PointF(9.0f,9.1f));
        pointView.setPoint(MarkReader.convertPoint(leftArea,info),0x30ff0000);

        List<PointF> leftArea2 = new ArrayList<PointF>();
        leftArea2.add(new PointF(11.6f,9.1f));
        leftArea2.add(new PointF(11.6f,27.0f));
        leftArea2.add(new PointF(18.8f,27.0f));
        leftArea2.add(new PointF(18.8f,9.1f));
        pointView.setPoint(MarkReader.convertPoint(leftArea2 ,info),0x30ff0000);

        List<PointF> leftArea3 = new ArrayList<PointF>();
        leftArea3.add(new PointF(11.6f,2.4f));
        leftArea3.add(new PointF(11.6f,7.1f));
        leftArea3.add(new PointF(13.8f,7.1f));
        leftArea3.add(new PointF(13.8f,2.4f));
        pointView.setPoint(MarkReader.convertPoint(leftArea3 ,info),0x30ff0000);

        List<PointF> leftArea4 = new ArrayList<PointF>();
        leftArea4.add(new PointF(14.5f,2.4f));
        leftArea4.add(new PointF(14.5f,7.1f));
        leftArea4.add(new PointF(18.8f,7.1f));
        leftArea4.add(new PointF(18.8f,2.4f));
        pointView.setPoint(MarkReader.convertPoint(leftArea4 ,info),0x30ff0000);


        //マークが読み込めていたら解答を出力
        List<Boolean> ansers = anserData.ansers;

        if(ansers != null) {
            int rows = ansers.size() / 20;
            for (int j = 0; j < rows; j++) {
                for (int i = 0; i < 10; i++)
                    sb.append(String.format("%s", ansers.get(j * 10 + i).booleanValue() ? "■" : "□"));
                sb.append("　");
                for (int i = 0; i < 10; i++)
                    sb.append(String.format("%s", ansers.get((j+40) * 10 + i).booleanValue() ? "■" : "□"));
                sb.append("\n");
            }
            TextView textView = (TextView) getView().findViewById(R.id  .textView2);
            textView.setText(sb.toString());
            if(anserData.numbers!=null&&anserData.ansers != null)
            Agg(convers(anserData.numbers,anserData.ansers));
        }

    }

    //読み取ったデータの変換
    public ArrayList<String> convers(List<Boolean> numbers, List<Boolean> ansers) {
        ArrayList<String> sendData = new ArrayList<String>();

        //試験番号格納変数
        String s1 = "";
        String s2 = "";
        String s3 = "";
        //学籍番号格納変数
        String g1 = "";
        String g2 = "";
        String g3 = "";
        String g4 = "";
        String g5 = "";
        String g6 = "";

        for (int j = 0; j < 100; j = j + 10) {
            for (int i = 0; i < 10; i++) {//試験番号、学籍番号の取得 i=横座標　j=縦座標
                if (numbers.get(i) == true) {
                    int a = j / 10 + 1;
                    if (a == 10)
                        a = 0;
                    switch (i) {
                        case 0:
                            s1 = String.valueOf(a);
                        case 1:
                            s2 = String.valueOf(a);
                        case 2:
                            s3 = String.valueOf(a);
                        case 4:
                            g1 = String.valueOf(a);
                        case 5:
                            g2 = String.valueOf(a);
                        case 6:
                            g3 = String.valueOf(a);
                        case 7:
                            g4 = String.valueOf(a);
                        case 8:
                            g5 = String.valueOf(a);
                        case 9:
                            g6 = String.valueOf(a);
                    }
                }
            }
        }

        String gNo = g1+g2+g3+g4+g5+g6;
        String sNo = s1+s2+s3;
        sendData.add(gNo);
        sendData.add(sNo);


        for (int j = 0; j < ansers.size(); j = j + 10) {//ansersの処理
            for (int i = 0; i < 10; i++) {
                if (ansers.get(i + j) == true) {
                    switch (i) {
                        case 0:
                            sendData.add("ア");
                            break;
                        case 1:
                            sendData.add("イ");
                            break;
                        case 2:
                            sendData.add("ウ");
                            break;
                        case 3:
                            sendData.add("エ");
                            break;
                        case 4:
                            sendData.add("オ");
                            break;
                        case 5:
                            sendData.add("カ");
                            break;
                        case 6:
                            sendData.add("キ");
                            break;
                        case 7:
                            sendData.add("ク");
                            break;
                        case 8:
                            sendData.add("ケ");
                            break;
                        case 9:
                            sendData.add("コ");
                            break;
                    }
                } else {
                    if (i == 9) {//無回答の場合
                        sendData.add(" ");
                        break;
                    }
                }
            }
        }


        return sendData;
    }

}
