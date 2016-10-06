package jp.ac.chiba_fjb.example.googlescript;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

/**
 * Created by oikawa on 2016/10/06.
 */

public class AppFinger {
    static public String getSha1(Context con){
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
}
