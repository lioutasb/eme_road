package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;


public class MainActivity extends ActionBarActivity {

    Activity act = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(getResources().getDrawable(R.drawable.ic_action_pin));

        GridView grid = (GridView) findViewById(R.id.gridview);
        grid.setAdapter(new MainGridAdapter(this));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0) {
                    Intent i = new Intent(act, SubmitActivity.class);
                    act.startActivity(i);
                }
                else if(position==1){
                    Intent i = new Intent(act, NearbyIncidentsMapActivity.class);
                    act.startActivity(i);
                }
                else if(position==2){
                    Intent i = new Intent(act, SearchActivity.class);
                    act.startActivity(i);
                }
                else if(position==3){
                    Intent i = new Intent(act, InfoActivity.class);
                    act.startActivity(i);
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_logout){
            new PersistentCookieStore(act).clear();
            Intent i = act.getBaseContext().getPackageManager()
                    .getLaunchIntentForPackage(act.getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            act.finish();
            act.startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
