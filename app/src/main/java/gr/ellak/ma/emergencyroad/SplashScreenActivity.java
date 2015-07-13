package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by billiout on 12/7/2015.
 */
public class SplashScreenActivity extends Activity {
    Activity act = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (JSONParser.isNetworkAvailable(this)) {
            new GetInfoForStart().execute();
        }
    }

    class GetInfoForStart extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            JSONParser sh = new JSONParser();

            try {
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                JSONObject jsonObject = sh.makeHttpRequest(act, "http://titansoft.netau.net/login_user.php", "GET", params1);
                if(jsonObject.getString("code").equals("1")){
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean response){
            if(response){
                Intent i = new Intent(act, MainActivity.class);
                act.startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                act.finish();
            }
            else{
                Intent i = new Intent(act, StartScreenActivity.class);
                act.startActivity(i);
                overridePendingTransition(0, 0);
                act.finish();
            }
        }
    }
}
