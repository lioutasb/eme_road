package gr.ellak.ma.emergencyroad;

import android.app.Application;
import android.content.res.Configuration;

import org.apache.http.impl.client.DefaultHttpClient;

/**
 * Created by billiout on 5/3/2015.
 */
public class EmeRoadApplication extends Application {
    private static EmeRoadApplication singleton;
    private static DefaultHttpClient httpClient;

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static EmeRoadApplication getInstance(){
        return singleton;
    }

    public DefaultHttpClient getHttpClient(){
        if(httpClient == null) {
            httpClient = new DefaultHttpClient();
            httpClient.setCookieStore(new PersistentCookieStore(getApplicationContext()));
        }
        return httpClient;
    }
}
