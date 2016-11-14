package jp.ac.chiba_fjb.example.googlescript;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oikawa on 2016/10/21.
 */

public class PointView extends View {


    class PointData{
        public PointData(List<Point> p,int c){
            points = p;
            color = c;
        }
        public List<Point> points;
        public int color;
    }

    private List<PointData> mPoints = new ArrayList<PointData>();
    private List<PointData> mStaticPoints = new ArrayList<PointData>();

    public PointView(Context con, AttributeSet attributeSet){
        super(con,attributeSet);
        setWillNotDraw(false);
    }
    public void setPoint(List<Point> points,int color){
        mPoints.add(new PointData(points,color));
        invalidate();
    }
    public void setStaticPoint(List<Point> points, int color) {
        mStaticPoints.add(new PointData(points,color));
        invalidate();
    }
    public void clear(){
        mPoints.clear();
    }
    private void draw(Canvas canvas,List<PointData> pd){
        for(PointData list :pd ) {
            Paint paint = new Paint();
            paint.setColor(list.color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

            for (int i = 0; i < list.points.size(); i++) {
                Point p1 = list.points.get(i);
                Point p2 = list.points.get((i + 1) % list.points.size());
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
            }
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        if(mPoints == null)
            return;
        draw(canvas,mPoints);
        draw(canvas,mStaticPoints);


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        int width = (int)(getWidth()*0.85f);
        int height = (int)(width* MarkReader.MarkerInfo.getAreaHeight()/ MarkReader.MarkerInfo.getAreaWidth());
        int marginX = (getWidth()-width)/2;
        int marginY = (getHeight()-height)/2;


        List<Point> area = new ArrayList<Point>();
        area.add(new Point(marginX,marginY));
        area.add(new Point(marginX,getHeight()-marginY));
        area.add(new Point(getWidth()-marginX,getHeight()-marginY));
        area.add(new Point(getWidth()-marginX,marginY));

        mStaticPoints.clear();
        setStaticPoint(area,0xaa00ff00);


    }
}
