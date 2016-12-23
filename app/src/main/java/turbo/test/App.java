package turbo.test;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "4gIMKHui5UNHQrgUSDTO6SKV-gzGzoHsz", "lQMPomTYALMxQLXN7fXicqT6");
    }
}
