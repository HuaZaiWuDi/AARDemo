package com.vondear.rxtools.utils;

import android.app.Application;
import android.content.pm.Signature;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.security.auth.x500.X500Principal;

/**
 * detail: 签名工具类（获取app，签名信息）
 * Created by Ttt
 */
public final class RxSignaturesUtils {

    private RxSignaturesUtils() {
    }

    // 日志TAG
    private static final String TAG = RxSignaturesUtils.class.getSimpleName();

    // 如需要小写则把ABCDEF改成小写
    private static final char HEX_DIGITS[] = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 检测应用程序是否是用"CN=Android Debug,O=Android,C=US"的debug信息来签名的
     * 判断签名是debug签名还是release签名
     */
    private final static X500Principal DEBUG_DN = new X500Principal("CN=Android Debug,O=Android,C=US");


    public static void logApkInfo(Application application) {
        RxLogUtils.d("MD5:" + signatureMD5(application));
        RxLogUtils.d("SHA1:" + signatureSHA1(application));
        RxLogUtils.d("SHA256:" + signatureSHA256(application));
        RxLogUtils.d("是否是DeBug版本:" + isDebuggable(application));
        printSignatureName(application);
    }

    /**
     * 进行转换
     *
     * @param bData
     * @return
     */
    public static String toHexString(byte[] bData) {
        StringBuilder sb = new StringBuilder(bData.length * 2);
        for (int i = 0, len = bData.length; i < len; i++) {
            sb.append(HEX_DIGITS[(bData[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[bData[i] & 0x0f]);
        }
        return sb.toString();
    }

    /**
     * 返回MD5
     *
     * @param application
     * @return
     */
    public static String signatureMD5(Application application) {
        Signature[] signatures = getSignaturesFromApk(getAppAPKFile(application));
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            if (signatures != null) {
                for (Signature s : signatures)
                    digest.update(s.toByteArray());
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            RxLogUtils.e(e);
            return "";
        }
    }

    /**
     * SHA1
     */
    public static String signatureSHA1(Application application) {
        Signature[] signatures = getSignaturesFromApk(getAppAPKFile(application));

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            if (signatures != null) {
                for (Signature s : signatures)
                    digest.update(s.toByteArray());
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            RxLogUtils.e(e);
            return "";
        }
    }

    /**
     * SHA256
     */
    public static String signatureSHA256(Application application) {
        Signature[] signatures = getSignaturesFromApk(getAppAPKFile(application));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            if (signatures != null) {
                for (Signature s : signatures)
                    digest.update(s.toByteArray());
            }
            return toHexString(digest.digest());
        } catch (Exception e) {
            RxLogUtils.e(e);
            return "";
        }
    }

    /**
     * 判断签名是debug签名还是release签名
     *
     * @return true = 开发(debug.keystore)，false = 上线发布（非.android默认debug.keystore）
     */
    public static boolean isDebuggable(Application application) {
        Signature[] signatures = getSignaturesFromApk(getAppAPKFile(application));
        // 判断是否默认key(默认是)
        boolean debuggable = true;
        try {
            for (int i = 0, len = signatures.length; i < len; i++) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
                debuggable = cert.getSubjectX500Principal().equals(DEBUG_DN);
                if (debuggable) {
                    break;
                }
            }
        } catch (Exception e) {
            RxLogUtils.e(e);
        }
        return debuggable;
    }

    /**
     * 获取App 证书对象
     */
    public static X509Certificate getX509Certificate(Application application) {

        Signature[] signatures = getSignaturesFromApk(getAppAPKFile(application));
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            ByteArrayInputStream stream = new ByteArrayInputStream(signatures[0].toByteArray());
            X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);
            return cert;
        } catch (Exception e) {
            RxLogUtils.e(e);
        }
        return null;
    }

    /**
     * 打印签名信息
     *
     * @param application
     * @return
     */
    public static void printSignatureName(Application application) {

        Signature[] signatures = getSignaturesFromApk(getAppAPKFile(application));
        try {
            for (int i = 0, len = signatures.length; i < len; i++) {
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                ByteArrayInputStream stream = new ByteArrayInputStream(signatures[i].toByteArray());
                X509Certificate cert = (X509Certificate) cf.generateCertificate(stream);

                String pubKey = cert.getPublicKey().toString(); // 公钥
                String signNumber = cert.getSerialNumber().toString();

                RxLogUtils.d(TAG, "signName:" + cert.getSigAlgName());//算法名
                RxLogUtils.d(TAG, "pubKey:" + pubKey);
                RxLogUtils.d(TAG, "signNumber:" + signNumber);//证书序列编号
                RxLogUtils.d(TAG, "subjectDN:" + cert.getSubjectDN().toString());
                RxLogUtils.d(TAG, cert.getNotAfter() + "--" + cert.getNotBefore());
            }
        } catch (Exception e) {
            RxLogUtils.e(e);
        }
    }


    public static File getAppAPKFile(Application application) {
        String path = application.getPackageResourcePath();
        return new File(path);
    }


    // --

    // Android的APK应用签名机制以及读取签名的方法
    // http://www.jb51.net/article/79894.htm

    /**
     * 从APK中读取签名
     *
     * @param file
     * @return
     */
    public static Signature[] getSignaturesFromApk(File file) {
        try {
            Certificate[] certificates = getCertificateFromApk(file);
            Signature[] signatures = new Signature[]{new Signature(certificates[0].getEncoded())};
            return signatures;
        } catch (Exception e) {
            RxLogUtils.e(e);
        }
        return null;
    }

    /**
     * 从APK中读取证书
     *
     * @param file
     * @return
     */
    public static Certificate[] getCertificateFromApk(File file) {
        try {
            JarFile jarFile = new JarFile(file);
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            return loadCertificates(jarFile, je, readBuffer);
        } catch (Exception e) {
            RxLogUtils.e(e);
        }
        return null;
    }

    /**
     * 加载证书
     *
     * @param jarFile
     * @param je
     * @param readBuffer
     * @return
     */
    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (Exception e) {
            RxLogUtils.e(e);
        }
        return null;
    }
}