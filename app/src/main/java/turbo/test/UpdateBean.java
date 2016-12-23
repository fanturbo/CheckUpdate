package turbo.test;

import java.io.Serializable;

/**
 * Created by turbo on 2016/9/18.
 */
public class UpdateBean implements Serializable {
    private int versionCode, size;
    private String versionName, url, note, md5;

    public UpdateBean() {
    }

    public UpdateBean(int versionCode, int size, String versionName, String url, String note, String md5) {
        this.versionCode = versionCode;
        this.size = size;
        this.versionName = versionName;
        this.url = url;
        this.note = note;
        this.md5 = md5;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
