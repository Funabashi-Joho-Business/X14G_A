package jp.ac.chiba_fjb.example.googlescript;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import jp.ac.chiba_fjb.example.googlescript.Fragment.TopFragment;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainLayout, new TopFragment(), TopFragment.class.getName());
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onBackPressed() {
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCnt != 0) {
            getSupportFragmentManager().popBackStack();
        } else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        for(Fragment f : getSupportFragmentManager().getFragments())
            if(f!=null)
                f. onActivityResult(requestCode, resultCode, data);

                        super.onActivityResult(requestCode, resultCode, data);
            }
//
//    public static void anserSend(List<Boolean> numbers, List<Boolean> ansers) {
//        ArrayList<String> sendData = new ArrayList<String>();
//
//        for (int j = 0; j < ansers.size(); j = j + 10) {//ansersの処理
//            for (int i = 0; i < 10; i++) {
//                if (ansers.get(i + j) == true) {
//                    switch (i) {
//                        case 0:
//                            sendData.add("ア");
//                            break;
//                        case 1:
//                            sendData.add("イ");
//                            break;
//                        case 2:
//                            sendData.add("ウ");
//                            break;
//                        case 3:
//                            sendData.add("エ");
//                            break;
//                        case 4:
//                            sendData.add("オ");
//                            break;
//                        case 5:
//                            sendData.add("カ");
//                            break;
//                        case 6:
//                            sendData.add("キ");
//                            break;
//                        case 7:
//                            sendData.add("ク");
//                            break;
//                        case 8:
//                            sendData.add("ケ");
//                            break;
//                        case 9:
//                            sendData.add("コ");
//                            break;
//
//                    }
//                } else {
//                    if (i == 9) {//無回答の場合
//                        sendData.add(" ");
//                        break;
//                    }
//                }
//            }
//        }
//
//        //試験番号格納変数
//        String s1 = "";
//        String s2 = "";
//        String s3 = "";
//        //学籍番号格納変数
//        String g1 = "";
//        String g2 = "";
//        String g3 = "";
//        String g4 = "";
//        String g5 = "";
//        String g6 = "";
//
//        for (int j = 0; j < 100; j = j + 10) {
//            for (int i = 0; i < 10; i++) {//試験番号、学籍番号の取得 i=横座標　j=縦座標
//                if (numbers.get(i) == true) {
//                    int a = j / 10 + 1;
//                    if (a == 10)
//                        a = 0;
//                    switch (i) {
//                        case 0:
//                            s1 = String.valueOf(a);
//                        case 1:
//                            s2 = String.valueOf(a);
//                        case 2:
//                            s3 = String.valueOf(a);
//                        case 4:
//                            g1 = String.valueOf(a);
//                        case 5:
//                            g2 = String.valueOf(a);
//                        case 6:
//                            g3 = String.valueOf(a);
//                        case 7:
//                            g4 = String.valueOf(a);
//                        case 8:
//                            g5 = String.valueOf(a);
//                        case 9:
//                            g6 = String.valueOf(a);
//                    }
//                }
//            }
//        }
//        String sNo = s1+s2+s3;
//        String gNo = g1+g2+g3+g4+g5+g6;
//        sendData.add(gNo);
//        sendData.add(sNo);
//
//    }
}