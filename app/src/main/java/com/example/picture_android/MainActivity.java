package com.example.picture_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.
        view.Display;
import android.view.View;
import android.widget.RelativeLayout;

import junit.framework.Test;


public class MainActivity extends Activity {
    private int x,y;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 //     setContentView(R.layout.activity_main);
        TestView testView = new TestView(this);
        setContentView(testView);
    }

    class TestView extends View {
        Paint paint;
        private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        public TestView(Context context) {
            super(context);
            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            //画面のサイズ
            x=TestView.this.getWidth();
            y=TestView.this.getHeight();

            // 背景
            canvas.drawColor(Color.argb(255, 0, 0, 125));
            //p_text(value,x,y,size,Color,canvas)
            p_text("x:" + x + "y:" + y, 500, 500, 35,Color.WHITE,canvas);
            //p_rect(left,top,right,bottom,color,canvas)
            p_rect(0,0,47,40,Color.BLACK,canvas);
            p_rect(0,y-40,47,y,Color.BLACK,canvas);
            p_rect(x-47,y-40,x,y,Color.BLACK,canvas);

        }

        protected void p_text(String value ,int tX,int tY,int size,int color,Canvas canvas){
            textPaint.setTextSize(size);
            textPaint.setColor(color);
            canvas.drawText(value,tX,tY,textPaint);
        }

        protected void p_rect(int left,int top,int right,int bottom,int color,Canvas canvas){
            Rect rectF = new Rect(left, top, right, bottom);
            paint.setColor(color);
            canvas.drawRect(rectF, paint);
        }
    }
}