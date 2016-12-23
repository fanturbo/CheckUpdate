package turbo.update;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.*;
import android.content.pm.*;
import android.net.*;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;

import java.io.*;
import java.math.*;
import java.nio.*;
import java.nio.channels.*;
import java.security.*;
import java.text.*;
import java.util.*;


/**
 * Created by turbo on 2016/09/14.
 */
public class AutoUpdateUtils {

    public static AlertDialog.Builder showDialog(Context context, String message, DialogInterface.OnClickListener onClickListener,boolean forceUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton("确定", onClickListener);
        if(!forceUpdate) {
            builder.setNegativeButton("取消", null);
        }
        builder.setCancelable(!forceUpdate);
        return builder;
    }

    protected static void downLoadApk(Context context, final String upgradeUrl, String message, String md5) {
        // Check if download manager is available:
        int state = context.getPackageManager().getApplicationEnabledSetting(
                "com.android.providers.downloads");
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {

            // Cannot download using download manager
            String packageName = "com.android.providers.downloads";
            try {
                // Open the specific App Info page:
                Intent settingIntent = new Intent(
                        android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                settingIntent.setData(Uri.parse("package:" + packageName));
                context.startActivity(settingIntent);

            } catch (ActivityNotFoundException e) {
                // Open the generic Apps page:
                Intent settingIntent = new Intent(
                        android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                context.startActivity(settingIntent);
            }
        } else {
            DownloadManager dManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(upgradeUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // 设置下载路径和文件名
            // 判断下权限
            File skRoot = Environment.getExternalStorageDirectory();
            String downloadPath = skRoot + "/Download";
            File fileDir = new File(downloadPath);
            if (Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                if (fileDir.exists() && fileDir.isDirectory()) {
                } else {
                    fileDir.mkdirs();
                }
            }
            request.setDestinationInExternalPublicDir(fileDir.getName(), getApplicationName(context) + ".apk");
            request.setDescription(message);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            // 设置为可被媒体扫描器找到
            request.allowScanningByMediaScanner();
            // 设置为可见和可管理
            request.setVisibleInDownloadsUi(true);
            long refernece = dManager.enqueue(request);
            // 把当前下载的ID保存起来
            context.getSharedPreferences("config", Context.MODE_PRIVATE).edit().putString("md5", md5).commit();
            context.getSharedPreferences("config", Context.MODE_PRIVATE).edit().putString("plato", refernece + "").commit();
        }
    }

    /**
     * 检查更新的方法
     *
     * @param context     context
     * @param forceUpdate 是否强制更新
     * @param versionCode versionCode
     * @param note        更新说明信息
     * @param url         新apk下载地址
     */
    public static void update(final Context context, boolean forceUpdate, final int versionCode, final String note, final String url) {
        final int currentCode = getVersionCode(context.getApplicationContext());
        if (currentCode < versionCode) {
            final SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            int lastIgnoredDayBegin = sp.getInt("time", 0);
            int todayBegin = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            if (!forceUpdate && todayBegin == lastIgnoredDayBegin)
                return;
            showDialog(context, note, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (url != null) {
                        context.getSharedPreferences("config", Context.MODE_PRIVATE).edit().putInt("time", Calendar.getInstance().get(Calendar.DAY_OF_YEAR)).commit();
                        downLoadApk(context, url, getApplicationName(context) + "新版本下载","");
                    }
                }
            },forceUpdate).show();
        }
    }

    /**
     * 检查更新的方法
     *
     * @param context     context
     * @param forceUpdate 是否强制更新
     * @param versionCode versionCode
     * @param note        更新说明信息
     * @param url         新apk下载地址
     * @param md5         md5
     */
    public static void update(final Context context, boolean forceUpdate, final int versionCode, final String note, final String url, final String md5) {
        final int currentCode = getVersionCode(context.getApplicationContext());
        if (currentCode < versionCode) {
            final SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
            int lastIgnoredDayBegin = sp.getInt("time", 0);
            int todayBegin = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            if (!forceUpdate && todayBegin == lastIgnoredDayBegin)
                return;
            showDialog(context, note, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (url != null) {
                        context.getSharedPreferences("config", Context.MODE_PRIVATE).edit().putString("time", Calendar.getInstance().get(Calendar.DAY_OF_YEAR) + "").commit();
                        downLoadApk(context, url, getApplicationName(context) + "新版本下载", md5);
                    }
                }
            },forceUpdate).show();
        }
    }

    public static boolean checkMD5(File file, String md5) {
        String md5Value = getMd5ByFile(file);
        return md5Value.equals(md5);
    }

    public static String getMd5ByFile(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            MappedByteBuffer byteBuffer = fis.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            return bi.toString(16);
        } catch (Throwable e) {
            e.printStackTrace();
            return "";
        } finally {
            closeQuietly(fis);
        }
    }

    public static String getApplicationName(Context app) {
        PackageManager packageManager;
        ApplicationInfo applicationInfo;
        try {
            packageManager = app.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(app.getPackageName(), 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static int getVersionCode(Context app) {
        int versionCode = 0;
        PackageManager manager = app.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(app.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static String getVersionName(Context app) {
        String versionName = "";
        PackageManager manager = app.getPackageManager();
        PackageInfo info;
        try {
            info = manager.getPackageInfo(app.getPackageName(), 0);
            versionName = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static int getAppIconResId(Context app) {
        int id = 0;
        PackageManager pm = app.getPackageManager();
        String pkg = app.getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
            id = ai.icon;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return id;
    }

    public static void closeQuietly(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (Throwable ignored) {
            }
        }
    }
}
