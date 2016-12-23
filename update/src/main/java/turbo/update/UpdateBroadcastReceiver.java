package turbo.update;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
    @SuppressLint("NewApi")
    public void onReceive(Context context, Intent intent) {
        long myDwonloadID = intent.getLongExtra(
                DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        String plato = context.getSharedPreferences("config", 0).getString("plato", "");
        String md5Value = context.getSharedPreferences("config", 0).getString("md5", "");
        File skRoot = Environment.getExternalStorageDirectory();
        String downloadPath = skRoot + "/Download";
        File fileDir = new File(downloadPath, AutoUpdateUtils.getApplicationName(context) + ".apk");
        if (!"".equals(md5Value) && AutoUpdateUtils.checkMD5(fileDir, md5Value)) {
            if ("".equals(plato)) {
                Toast.makeText(context, "下载新版本失败，请稍后重试", Toast.LENGTH_SHORT).show();
                return;
            }
            long refernece = Long.parseLong(plato);
            if (refernece == myDwonloadID) {
                try {
                    String serviceString = Context.DOWNLOAD_SERVICE;
                    DownloadManager dManager = (DownloadManager) context
                            .getSystemService(serviceString);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    Uri downloadFileUri = dManager
                            .getUriForDownloadedFile(myDwonloadID);
                    install.setDataAndType(downloadFileUri,
                            "application/vnd.android.package-archive");
                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(context, "安装APP失败", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(context, "您下载的版本有问题,请到官网进行下载", Toast.LENGTH_SHORT).show();
        }

    }
}
