package jp.ac.chiba_fjb.example.googlescript.Camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jp.ac.chiba_fjb.example.googlescript.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends Fragment implements CameraPreview.SaveListener, SeekBar.OnSeekBarChangeListener {

    private boolean mActive;
    private CameraPreview mCamera;
    private SeekBar mSeekBar;
    private SaveListener mSaveListener;
    private int mVisibility;
    private boolean mStopFlag;

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
        View view =  inflater.inflate(R.layout.fragment_camera, container, false);
        mSeekBar = (SeekBar)view.findViewById(R.id.seekBar);
        mSeekBar.setProgress(110);
        mSeekBar.setOnSeekBarChangeListener(this);

        TextView textView = (TextView) view.findViewById(R.id.textView);
        textView.setText(""+110);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mCamera = new CameraPreview();
        mCamera.setSaveListener(this);
        TextureView textureView = (TextureView)getView().findViewById(R.id.textureView);
        mCamera.setTextureView(textureView);
        mCamera.open(0);


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point real = getRealSize(getActivity());
        float aspect;

        Configuration config = getResources().getConfiguration();
        if(config.orientation == Configuration.ORIENTATION_PORTRAIT)
            aspect = (float)real.y/real.x;
        else
            aspect = (float)real.x/real.y;

        mCamera.setPreviewSize(1280,(int)(1280/aspect));
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

        mCamera.setLight(true);
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
                if(info.markerCount < 4)
                {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(getView()!=null){
                                TextView textView = (TextView) getView().findViewById(R.id  .textView2);
                                if(textView != null)
                                    textView.setText(String.format("検出マーク数: %d",info.markerCount));
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
        }
    }

    public ArrayList<String> anserSend(List<Boolean> numbers, List<Boolean> ansers) {
        ArrayList<String> sendData = new ArrayList<String>();

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
        String sNo = s1+s2+s3;
        String gNo = g1+g2+g3+g4+g5+g6;
        sendData.add(gNo);
        sendData.add(sNo);
        return sendData;
    }
}
