package jp.ac.chiba_fjb.example.googlescript.Camera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by oikawa on 2016/10/19.
 */

public class MarkReader {



    public static class AnserData{
        public List<Boolean> numbers;
        public  List<Boolean> ansers;
    }


    public static boolean isInner(Rect rect1,Rect rect2){
        if(rect1.x<rect2.x && rect1.y<rect2.y && rect1.x+rect1.width > rect2.x+rect2.width && rect1.y+rect1.height > rect2.y+rect2.height)
            return true;
        return false;
    }
    public static Bitmap monocrome(Bitmap bmp,int limit) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int pixels[] = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);


        // モノクロにする処理
        Bitmap outBitMap = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);

        int i, j;
        for (j = 0; j < height; j++) {
            for (i = 0; i < width; i++) {
                int pixelColor = pixels[j*width+i];
                int c = Color.red(pixelColor)<limit && Color.green(pixelColor)<limit && Color.blue(pixelColor)<limit?0:0xffffff;
                pixels[j*width+i] = c;
            }
        }
        outBitMap.setPixels(pixels, 0, width, 0, 0, width, height);
        return outBitMap;
    }

    public static List<MatOfPoint> getCountours(Bitmap bmp,int limit){
        Mat image32S = new Mat();
        Utils.bitmapToMat(bmp,image32S);

        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        Mat hierarchy = new Mat();
        Imgproc.cvtColor(image32S, image32S, Imgproc.COLOR_RGB2GRAY);
        Imgproc.threshold(image32S, image32S, limit, 255, Imgproc.THRESH_BINARY);//|Imgproc.THRESH_OTSU
        Imgproc.findContours(image32S, contours,  hierarchy, Imgproc.RETR_LIST,Imgproc.CHAIN_APPROX_NONE);

        return contours;

    }
    public static Point getNearPoint(List<Point> pointList,int x,int y){
        int near=0;
        Point point = null;
        for(Point p : pointList){
            int n = (p.x - x)*(p.x - x) + (p.y - y)*(p.y - y);
            if(point==null || n< near) {
                point = p;
                near = n;
            }
        }
        return point;
    }
    public static Rect getNearRect(List<Rect> pointList,int x,int y){
        int near=0;
        Rect point = null;
        for(Rect p : pointList){
            int px = p.x + p.width/2;
            int py = p.y + p.height/2;

            int n = (px - x)*(px - x) + (py - y)*(py - y);
            if(point==null || n< near) {
                point = p;
                near = n;
            }
        }
        return point;
    }
    static public class MarkerInfo{
        public int markerCount;
        public Point[] markerPoint = new Point[4];           //左上,左下,右下,右上の位置
        public double[] markerAngle = new double[4];      //同上、角度
        public double angle;

        public static float getAreaWidth(){return 18.9f;}
        public static float getAreaHeight(){return 28.3f;}
    }

    public static MarkerInfo getMarker(Bitmap bitmap,int limit){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        //カメラデータの長い方の辺を30cmと想定
        float ap = Math.max(width,height)/30.0f;

        //輪郭を抽出
        List<MatOfPoint> points = getCountours(bitmap,limit);
        List<MatOfPoint> outPoint = filter(points, 0.9*ap,0.9*ap,0.5,0.5);
        List<MatOfPoint> inPoint = filter(points, 0.5*ap,0.5*ap,0.5,0.5);

        List<Rect> boxPoint = new ArrayList<Rect>();

        int i,j;
        for(j=0;j<outPoint.size();j++) {
            if(outPoint.get(j) == null)
                continue;
            Rect rect1 = Imgproc.boundingRect(outPoint.get(j));

            for (i = 0; i < inPoint.size(); i++) {
                if(inPoint.get(i) == null)
                    continue;
                Rect rect2 = Imgproc.boundingRect(inPoint.get(i));

                if (isInner(rect1, rect2)) {
                    boxPoint.add(rect1);
                    outPoint.set(j,null);
                    inPoint.set(i,null);
                    break;
                }
            }
        }
        if(boxPoint.size()  >= 4){
            List<Rect> boxPoint2 = new ArrayList<Rect>();
            boxPoint2.add(getNearRect(boxPoint,0,0));
            boxPoint2.add(getNearRect(boxPoint,0,height));
            boxPoint2.add(getNearRect(boxPoint,width,height));
            boxPoint2.add(getNearRect(boxPoint,width,0));
            boxPoint = boxPoint2;
        }
        MarkerInfo info = new MarkerInfo();
        info.markerCount = boxPoint.size();
        if(boxPoint.size() != 4)
            return info;



        info.markerPoint[0] = new Point(boxPoint.get(0).x,boxPoint.get(0).y);
        info.markerPoint[1] = new Point(boxPoint.get(1).x,boxPoint.get(1).y+boxPoint.get(1).height);
        info.markerPoint[2] = new Point(boxPoint.get(2).x+boxPoint.get(2).width,boxPoint.get(2).y+boxPoint.get(2).height);
        info.markerPoint[3] = new Point(boxPoint.get(3).x+boxPoint.get(3).width,boxPoint.get(3).y);


        //角度を計算
        for(i=0;i<4;i++){
            int index1 = i-1<0?3:i-1;
            int index2 = i+1>3?0:i+1;

            double x1 = info.markerPoint[index1].x - info.markerPoint[i].x;
            double y1 = info.markerPoint[index1].y - info.markerPoint[i].y;
            double x2 = info.markerPoint[index2].x - info.markerPoint[i].x;
            double y2 = info.markerPoint[index2].y - info.markerPoint[i].y;
            info.markerAngle[i] = Math.acos((x1*x2+y1*y2)/Math.sqrt((x1*x1+y1*y1)*(x2*x2+y2*y2)))*180/Math.PI;
        }

        //直角と一辺から角度を算出
        double x1 = info.markerPoint[0].x - info.markerPoint[1].x;
        double y1 = info.markerPoint[0].y - info.markerPoint[1].y;
        double a = Math.atan2(y1,x1) * 180 / Math.PI+90;
        info.angle = a;

        return info;

    }


    public static List<MatOfPoint> filter(List<MatOfPoint> points,double width,double height,double rateX,double rateY){
        double minWidth = width*rateX;
        double minHeight = height*rateY;
        double maxWidth = width;
        double maxHeight = height;

        ArrayList<MatOfPoint> output = new ArrayList<MatOfPoint>();
        for( int i = 0; i< points.size(); i++ )
        {
            Rect rect = Imgproc.boundingRect(points.get(i));
            double area = Math.abs(rect.area());

            if(rect.width >= minWidth && rect.width <= maxWidth &&
                    rect.height >= minHeight && rect.height <= maxHeight){
                output.add(points.get(i));
            }
        }
        return output;
    }

    static PointF percentPoint(Point point1, Point point2, float per){
        return new PointF(
                point1.x+(point2.x-point1.x)*per,
                point1.y+(point2.y-point1.y)*per);
    }

    //線分から交点の計算
    static  Point cross(PointF p1,PointF p2,PointF p3,PointF p4)
    {
        float dev = (p2.y-p1.y)*(p4.x-p3.x)-(p2.x-p1.x)*(p4.y-p3.y);
        float d1 = (p3.y*p4.x-p3.x*p4.y);
        float d2 = (p1.y*p2.x-p1.x*p2.y);


        float px = d1*(p2.x-p1.x) - d2*(p4.x-p3.x);
        px /= dev;
        float py = d1*(p2.y-p1.y) - d2*(p4.y-p3.y);
        py /= dev;

        return new Point((int)px,(int)py);
    }

    static Point convertPoint(PointF p, MarkReader.MarkerInfo info){
        Point[] points = info.markerPoint;
        PointF p1 = percentPoint(points[0],points[1],p.y/info.getAreaHeight());
        PointF p2 = percentPoint(points[3],points[2],p.y/info.getAreaHeight());
        PointF p3 = percentPoint(points[0],points[3],p.x/info.getAreaWidth());
        PointF p4 = percentPoint(points[1],points[2],p.x/info.getAreaWidth());

        Point point = cross(p1,p2,p3,p4);

        return point;
    }
    static List<Point> convertPoint(List<PointF> list,MarkReader.MarkerInfo info){
        List<Point> out = new ArrayList<Point>();
        for(int i=0;i<list.size();i++){
            out.add(convertPoint(list.get(i),info));
        }
        return out;
    }
    public static List<Boolean> getMark(Bitmap bitmap,int rows,PointF start,PointF size,MarkReader.MarkerInfo info,int limit){
        List<Boolean> datas = new ArrayList<Boolean>();

        float marginX = size.x*0.20f;
        float marginY = size.y*0.4f;

        int i,j;
        for(j=0;j<rows;j++){
            for(i=0;i<10;i++){
                List<PointF> leftArea = new ArrayList<PointF>();
                leftArea.add(new PointF(start.x+size.x*(i+0)+marginX,start.y+size.y*(j+0)+marginY));
                leftArea.add(new PointF(start.x+size.x*(i+1)-marginX,start.y+size.y*(j+1)-marginY));
                List<Point> p = convertPoint(leftArea,info);



                int width = p.get(1).x-p.get(0).x;
                int height = p.get(1).y-p.get(0).y;
                int all = width*height;
                if(all <= 0)
                    return null;
                int[] pic = new int[all];

                bitmap.getPixels(pic,0,width,p.get(0).x,p.get(0).y,width,height);
                int count = 0;
                for(int c : pic){
                    if(Color.red(c) < limit && Color.green(c)<limit && Color.blue(c)<limit)
                        count++;
                }
                boolean flag = ((float)count)/(width*height)>0.0000001f;
                datas.add(flag);

            }
        }

        return datas;
    }
    public static AnserData getAnser(Bitmap bitmap, MarkerInfo info, int limit) {
        if(bitmap == null || info == null || info.markerCount != 4)
            return null;
        //矩形の各角度が0.8度以下なら検出を開始
        int i;
        for(i=0;i<4;i++)
            if(Math.abs(90.0f -info.markerAngle[i]) > 1.0f)
                break;

        AnserData anserData = new AnserData();

        List<Boolean> m = null;

        PointF size = new PointF(7.2f/10.0f,17.8f/40.0f);

        PointF start = new PointF(1.82f,9.1f);
        List<Boolean> ans;
        ans = MarkReader.getMark(bitmap, 40, start, size, info, limit);
        if(ans == null)
            return null;
        anserData.ansers = ans;

        start = new PointF(11.6f,9.1f);
        ans = MarkReader.getMark(bitmap,40, start, size, info, limit);
        if(ans == null)
            return null;
        anserData.ansers.addAll(ans);


        start = new PointF(11.6f,2.4f);
        ans = MarkReader.getMark(bitmap,10, start, size, info, limit);
        if(ans == null)
            return null;
        anserData.numbers = ans;

        return anserData;
    }
}
