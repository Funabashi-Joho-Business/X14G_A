package jp.ac.chiba_fjb.example.googlescript;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.api.services.script.model.Operation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;

import jp.ac.chiba_fjb.example.googlescript.Fragment.SyukeiFragment;
import jp.ac.chiba_fjb.example.googlescript.Fragment.TopFragment;

import static java.security.AccessController.getContext;


public class MainActivity extends AppCompatActivity {
//    final String[] SCOPES = {
//            "https://www.googleapis.com/auth/drive",
//            "https://www.googleapis.com/auth/script.storage",
//            "https://www.googleapis.com/auth/spreadsheets"};
//
//    private GoogleScript mGoogleScript;
//
//    private Handler mHandler = new Handler();
//    private View v;
//    private Bundle bundle;

    public static final String SCRIPT_URL =
            "1Xqgsbl5TcisKN1eAJLcIimVcUj6HtjREVe8Mia97yOANyas00pd9f1c2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        String s = getAppFinger(this);
        System.out.print(s);

//        ArrayList<Object> testCor = new ArrayList<Object>();
//        testCor.add("t5555");
//        testCor.add("2017/01/23");
//        testCor.add("x12d123");
//        testCor.add(10);
//        testCor.add("2017/01/01");
//        testCor.add("x12d456");
//        testCor.add(20);
//        testCor.add("2017/01/01");
//        testCor.add("x12d789");
//        testCor.add(30);
//        testCor.add("2017/01/01");
//        testCor.add("x13d123");
//        testCor.add(40);
//        testCor.add("2017/01/01");
//
//        ArrayList<ArrayList<Object>> allSend = new ArrayList<ArrayList<Object>>();
//
//        ArrayList<Object> test1 = new ArrayList<Object>();
//        test1.add("x12d123");
//        test1.add("t5555");
//        test1.add("2017/01/01");
//        test1.add("ア");
//        test1.add("ア");
//        test1.add("ウ");
//        test1.add("エ");
//        test1.add("");
//        test1.add("");
//
//        ArrayList<Object> test2 = new ArrayList<Object>();
//
//        test2.add("x12d456");
//        test2.add("t5555");
//        test2.add("2017/01/01");
//        test2.add("ア");
//        test2.add("ア");
//        test2.add("ウ");
//        test2.add("ウ");
//        test2.add("");
//        test2.add("");
//
//        allSend.add(test1);
//        allSend.add(test2);
//        sendGas(testCor,allSend);


        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainLayout, new TopFragment(), TopFragment.class.getName());
        ft.addToBackStack(null);
        ft.commit();
    }

//    public  void sendGas(ArrayList<Object> testCor, ArrayList<ArrayList<Object>> allSend){
//
//
//        List<Object> params = new ArrayList<>();
//        params.add("メディア");//テスト名
//        params.add(testCor);//試験別集計
//        params.add(allSend);//個人集計群
//
//
//
//
//        mGoogleScript = new GoogleScript(this,SCOPES);
//
//        mGoogleScript.execute(MainActivity.SCRIPT_URL, "getdata",
//                params, new GoogleScript.ScriptListener() {
//                    @Override
//                    public void onExecuted(GoogleScript script, final Operation op) {
//                        //   TextView textView = (TextView) findViewById(R.id.textMessage);
//                        mHandler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (op == null || op.getError() != null) {
//
//                                    System.out.println("Script:error"); //       textView.append("Script結果:エラー\n");
//                                } else {
//                                    //戻ってくる型は、スクリプト側の記述によって変わる
//                                    System.out.println("スクリプト成功");
//                                    ArrayList<ArrayList<String>> ansList = (ArrayList<ArrayList<String>>) op.getResponse().get("result");
//
//                                }
//                            }
//                        });
//                    }
//                });
//
//    }

    static public String getAppFinger(Context con){
        try {
            PackageInfo packageInfo = con.getPackageManager().getPackageInfo(con.getPackageName(), PackageManager.GET_SIGNATURES);
            InputStream input = new ByteArrayInputStream(packageInfo.signatures[0].toByteArray());
            Certificate c = CertificateFactory.getInstance("X509").generateCertificate(input);
            byte[] publicKey = MessageDigest.getInstance("SHA1").digest(c.getEncoded());

            StringBuffer hexString = new StringBuffer();
            for (int i=0;i<publicKey.length;i++)
                hexString.append(String.format("%02x",publicKey[i]));
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        int backStackCnt = getSupportFragmentManager().getBackStackEntryCount();
        if (backStackCnt > 1)
            getSupportFragmentManager().popBackStack();
        else
            finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        for(Fragment f : getSupportFragmentManager().getFragments()) {
            if (f != null)
                f.onActivityResult(requestCode, resultCode, data);
        }
//        mGoogleScript.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}