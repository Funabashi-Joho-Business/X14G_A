package jp.ac.chiba_fjb.example.googlescript;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import jp.ac.chiba_fjb.example.googlescript.Fragment.TopFragment;


public class MainActivity extends AppCompatActivity {
    public static final String SCRIPT_URL =
            "1Xqgsbl5TcisKN1eAJLcIimVcUj6HtjREVe8Mia97yOANyas00pd9f1c2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        String s = getAppFinger(this);
        System.out.print(s);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.mainLayout, new TopFragment(), TopFragment.class.getName());
        ft.addToBackStack(null);
        ft.commit();
    }

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
        super.onActivityResult(requestCode, resultCode, data);
    }
}