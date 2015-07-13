package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

/**
 * Created by billiout on 12/7/2015.
 */
public class StartScreenActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);



    }


    public void openLogin(View view){
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("page", 0);
        startActivity(i);
    }

    public void openRegister(View view){
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("page", 1);
        startActivity(i);
    }

}
