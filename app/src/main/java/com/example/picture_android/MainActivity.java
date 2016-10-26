package com.example.picture_android;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
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
        Paint mDotPaint = new Paint();

        public TestView(Context context) {
            super(context);
            paint = new Paint();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            String status[] = {"受験科目名","学科名","氏名"};
            //画面のサイズ
            x=TestView.this.getWidth();
            y=TestView.this.getHeight();

            // 背景
            canvas.drawColor(Color.argb(255,255,255,255));
            p_rect_border(15,100,465,215,Color.WHITE,canvas);
            //p_text(value,x,y,size,Color,canvas)
            p_text("x:" + x + "y:" + y, 500, 500, 35,Color.BLACK,canvas);
            //p_rect(left,top,right,bottom,color,canvas)
            p_rect(0,0,47,40,Color.BLACK,canvas);
            p_rect(0,y-40,47,y,Color.BLACK,canvas);
            p_rect(x-47,y-40,x,y,Color.BLACK,canvas);
            p_rect_border(15,100,160,215,Color.YELLOW,canvas);
            paint.setColor(Color.BLACK);

            //自己情報横線
            canvas.drawLine(15,140,465,140,paint);
            canvas.drawLine(15,180,465,180,paint);
            canvas.drawLine(15,215,465,215,paint);
            //学年、組
            p_rect_border(285,140,330,180,Color.YELLOW,canvas);
            p_rect_border(375,140,420,180,Color.YELLOW,canvas);
            //title
            p_text("解 　答 　用 　紙",50,60,28,Color.BLACK,canvas);

            //自己情報入力部分作成
            int ix=50,iy=120,f_size=14;
            for(String s :status){
                p_text(s,ix,iy,f_size,Color.BLACK,canvas);
                iy+=40;
            }

            //試験番号
            p_rect_border(485,20,575,280,Color.WHITE,canvas);
            p_rect(486,21,574,40,Color.argb(216,253,233,217),canvas);
            paint.setColor(Color.argb(219,250,191,143));
            canvas.drawLine(485,40,575,40,paint);
            canvas.drawLine(485,80,575,80,paint);

            canvas.drawLine(515,40,515,279,paint);
            canvas.drawLine(545,40,545,279,paint);

            p_text("試 験 番 号",505,35,11,Color.argb(255,226,107,10),canvas);
            //番号
            int number[] ={1,2,3,4,5,6,7,8,9,0};
            ix=495;
            f_size=11;
            for(int i=0;i<3;i++){
                iy=95;
                for(int num:number){
                    p_text("["+num+"]",ix,iy,f_size,Color.BLACK,canvas);
                    iy+=20;
                }
                ix+=30;
            }

            //学籍番号
            int g_x=580,g_y=20,g_dx=710,g_dy=280;
            p_rect_border(g_x,g_y,g_dx,g_dy,Color.WHITE,canvas);
            p_rect(581,21,709,279,Color.argb(216,253,233,217),canvas);
            p_rect(g_x+1,g_y+1,g_dx,g_y-1,Color.argb(216,253,233,217),canvas);
            paint.setColor(Color.argb(219,250,191,143));
            canvas.drawLine(g_x+10,g_y+20,g_dx,g_y+20,paint);
            int l_x=g_x+20,l_y=g_x+20,l_dy=279;
            canvas.drawLine(l_x,l_y,l_x,l_dy,paint);
            canvas.drawLine(l_x+20,l_y,l_x+20,l_dy,paint);
            canvas.drawLine(l_x+40,l_y,l_x+40,l_dy,paint);
            canvas.drawLine(l_x+60,l_y,l_x+60,l_dy,paint);
            canvas.drawLine(l_x+80,l_y,l_x+80,l_dy,paint);

        }

        //テキスト
        protected void p_text(String value ,int tX,int tY,int size,int color,Canvas canvas){
            textPaint.setTextSize(size);
            textPaint.setColor(color);
            canvas.drawText(value,tX,tY,textPaint);
        }

        //四角
        protected void p_rect(int left,int top,int right,int bottom,int color,Canvas canvas){
            Rect rectF = new Rect(left, top, right, bottom);
            paint.setColor(color);
            canvas.drawRect(rectF, paint);
        }

        //border付き四角
        protected void p_rect_border(int left,int top,int right,int bottom,int color,Canvas canvas){
            Rect rectF = new Rect(left, top, right, bottom);
            paint.setColor(color);
            canvas.drawRect(rectF, paint);

            paint.setColor(Color.BLACK);
            //左
            canvas.drawLine(left,top,left,bottom,paint);
            //上
            canvas.drawLine(left,top,right,top,paint);
            //右
            canvas.drawLine(right,top,right,bottom,paint);
            //下
            canvas.drawLine(left,bottom,right,bottom,paint);
        }

    }
}