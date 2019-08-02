//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.vondear.rxtools.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.zip.GZIPOutputStream;

public class EasyUtils {
    public static final String TAG = "EasyUtils";
    private static Hashtable<String, String> resourceTable = new Hashtable();

    public EasyUtils() {
    }

    public static boolean isAppRunningForeground(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService("activity");

        try {
            List var2 = var1.getRunningTasks(1);
            if (var2 != null && var2.size() >= 1) {
                boolean var3 = var0.getPackageName().equalsIgnoreCase(((RunningTaskInfo) var2.get(0)).baseActivity.getPackageName());
                RxLogUtils.d("utils", "app running in foregroud：" + var3);
                return var3;
            } else {
                return false;
            }
        } catch (SecurityException var4) {
            RxLogUtils.d("EasyUtils", "Apk doesn't hold GET_TASKS permission");
            var4.printStackTrace();
            return false;
        }
    }

    public static String getTopActivityName(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService("activity");

        try {
            List var2 = var1.getRunningTasks(1);
            return var2 != null && var2.size() >= 1 ? ((RunningTaskInfo) var2.get(0)).topActivity.getClassName() : "";
        } catch (SecurityException var3) {
            RxLogUtils.d("EasyUtils", "Apk doesn't hold GET_TASKS permission");
            var3.printStackTrace();
            return "";
        }
    }

    public static boolean isSingleActivity(Context var0) {
        ActivityManager var1 = (ActivityManager) var0.getSystemService("activity");
        List var2 = null;

        try {
            var2 = var1.getRunningTasks(1);
        } catch (SecurityException var4) {
            var4.printStackTrace();
        }

        if (var2 != null && var2.size() >= 1) {
            return ((RunningTaskInfo) var2.get(0)).numRunning == 1;
        } else {
            return false;
        }
    }

    public static List<String> getRunningApps(Context var0) {
        ArrayList var1 = new ArrayList();

        try {
            ActivityManager var2 = (ActivityManager) var0.getSystemService("activity");
            List var3 = var2.getRunningAppProcesses();
            if (var3 == null) {
                return var1;
            }

            Iterator var4 = var3.iterator();

            while (var4.hasNext()) {
                RunningAppProcessInfo var5 = (RunningAppProcessInfo) var4.next();
                String var6 = var5.processName;
                if (var6.contains(":")) {
                    var6 = var6.substring(0, var6.indexOf(":"));
                }

                if (!var1.contains(var6)) {
                    var1.add(var6);
                }
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return var1;
    }

    @SuppressLint({"SimpleDateFormat"})
    public static String getTimeStamp() {
        Date var0 = new Date(System.currentTimeMillis());
        SimpleDateFormat var1 = new SimpleDateFormat("yyyyMMddHHmmss");
        return var1.format(var0);
    }

    public static boolean writeToZipFile(byte[] var0, String var1) {
        FileOutputStream var2 = null;
        GZIPOutputStream var3 = null;

        label110:
        {
            boolean var5;
            try {
                var2 = new FileOutputStream(var1);
                var3 = new GZIPOutputStream(new BufferedOutputStream(var2));
                var3.write(var0);
                break label110;
            } catch (Exception var20) {
                var20.printStackTrace();
                var5 = false;
            } finally {
                if (var3 != null) {
                    try {
                        var3.close();
                    } catch (IOException var19) {
                        var19.printStackTrace();
                    }
                }

                if (var2 != null) {
                    try {
                        var2.close();
                    } catch (IOException var18) {
                        var18.printStackTrace();
                    }
                }

            }

            return var5;
        }

        if (true) {
            File var4 = new File(var1);
            DecimalFormat var22 = new DecimalFormat("#.##");
            double var6 = (double) var4.length() / (double) var0.length * 100.0D;
            double var8 = Double.valueOf(var22.format(var6));
            RxLogUtils.d("zip", "data size:" + var0.length + " zip file size:" + var4.length() + "zip file ratio%: " + var8);
        }

        return true;
    }

    public static String getAppResourceString(Context var0, String var1) {
        String var2 = (String) resourceTable.get(var1);
        if (var2 != null) {
            return var2;
        } else {
            int var3 = var0.getResources().getIdentifier(var1, "string", var0.getPackageName());
            var2 = var0.getString(var3);
            if (var2 != null) {
                resourceTable.put(var1, var2);
            }

            return var2;
        }
    }

    public static String convertByteArrayToString(byte[] var0) {
        StringBuilder var1 = new StringBuilder();
        byte[] var2 = var0;
        int var3 = var0.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            byte var5 = var2[var4];
            var1.append(String.format("0x%02X", var5));
        }

        return var1.toString();
    }

    public static boolean isSDCardExist() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    public static boolean copyFile(String var0, String var1) {
        boolean var2 = true;
        FileInputStream var3 = null;
        FileOutputStream var4 = null;

        try {
            int var5 = 0;
            boolean var6 = false;
            File var7 = new File(var0);
            if (var7.exists()) {
                var3 = new FileInputStream(var0);
                var4 = new FileOutputStream(var1);
                byte[] var8 = new byte[1024];

                int var23;
                while ((var23 = var3.read(var8)) != -1) {
                    var5 += var23;
                    var4.write(var8, 0, var23);
                }

                var4.flush();
            } else {
                var2 = false;
            }
        } catch (Exception var21) {
            var2 = false;
        } finally {
            if (var3 != null) {
                try {
                    var3.close();
                } catch (Exception var20) {
                }
            }

            if (var4 != null) {
                try {
                    var4.close();
                } catch (Exception var19) {
                }
            }

        }

        return var2;
    }

    public static boolean copyFolder(String var0, String var1) {
        boolean var2 = true;
        FileInputStream var3 = null;
        FileOutputStream var4 = null;

        try {
            (new File(var1)).mkdirs();
            File var5 = new File(var0);
            String[] var6 = var5.list();
            String var7 = null;
            File var8 = null;
            String[] var9 = var6;
            int var10 = var6.length;

            for (int var11 = 0; var11 < var10; ++var11) {
                String var12 = var9[var11];
                if (var0.endsWith(File.separator)) {
                    var7 = var0 + var12;
                } else {
                    var7 = var0 + File.separator + var12;
                }

                var8 = new File(var7);
                if (var8.isFile()) {
                    try {
                        var3 = new FileInputStream(var8);
                        var4 = new FileOutputStream(var1 + "/" + var8.getName());
                        byte[] var13 = new byte[5120];

                        int var14;
                        while ((var14 = var3.read(var13)) != -1) {
                            var4.write(var13, 0, var14);
                        }

                        var4.flush();
                    } catch (Exception var28) {
                        var2 = false;
                    } finally {
                        if (var3 != null) {
                            try {
                                var3.close();
                            } catch (Exception var27) {
                            }
                        }

                        if (var4 != null) {
                            try {
                                var4.close();
                            } catch (Exception var26) {
                            }
                        }

                    }
                }

                if (var8.isDirectory()) {
                    copyFolder(var0 + "/" + var12, var1 + "/" + var12);
                }
            }
        } catch (Exception var30) {
            var2 = false;
        }

        return var2;
    }

    public static String useridFromJid(String var0) {
        String var1 = "";
        if (var0.contains("_") && var0.contains("@easemob.com")) {
            var1 = var0.substring(var0.indexOf("_") + 1, var0.indexOf("@"));
        } else if (var0.contains("_")) {
            var1 = var0.substring(var0.indexOf("_") + 1);
        } else {
            var1 = var0;
        }

        return var1;
    }

    public static String getMediaRequestUid(String var0, String var1) {
        return var0 + "_" + var1;
    }
}
