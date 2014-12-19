package gr.ellak.ma.emergencyroad;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Billys on 18/12/2014.
 */
public class IncidentActivity extends ActionBarActivity {

    int id;
    JSONParser jsonParser = new JSONParser();
    TextView loc;
    TextView des;
    TextView date;
    TextView upTxt;
    TextView downTxt;
    ImageView upBtn;
    ImageView downBtn;
    Incident incident = new Incident();

    @Override
    protected void onCreate(Bundle saved){
        super.onCreate(saved);
        setContentView(R.layout.activity_incident);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Incident " + getIntent().getExtras().getString("title"));
        id = getIntent().getExtras().getInt("id");
        loc= (TextView)findViewById(R.id.loctittle);
        des= (TextView)findViewById(R.id.desc_txt);
        date=(TextView)findViewById(R.id.datetext);
        upTxt=(TextView)findViewById(R.id.up_vote_count);
        downTxt=(TextView)findViewById(R.id.down_vote_count);
        upBtn = (ImageView) findViewById(R.id.btn_up);
        downBtn = (ImageView) findViewById(R.id.btn_down);
        new GetIncidentDetails().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_incident, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_refresh) {
            new GetIncidentDetails().execute();
            return true;
        }
        else if(id == R.id.action_share){
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Emergency Road | " + incident.location;
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Emergency Road | Incident On " + incident.location + " (" + incident.date + ")");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share This Incident"));
        }

        return super.onOptionsItemSelected(item);
    }

    public class GetIncidentDetails extends AsyncTask<Void, Void, Incident>{

        @Override
        protected Incident doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("ID", String.valueOf(id)));
            JSONObject json = jsonParser.makeHttpRequest("http://titansoft.netau.net/get_incident_details.php",
                    "GET", params1);
            System.out.println(json.toString());
            Incident i= new Incident();
            try {
                i.ID= json.getJSONObject("incident").getInt("ID");
                i.location=json.getJSONObject("incident").getString("location");
                i.date=json.getJSONObject("incident").getString("date");
                i.description=json.getJSONObject("incident").getString("description");
                i.up_votes=json.getJSONObject("incident").getInt("up_vote");
                i.down_votes=json.getJSONObject("incident").getInt("down_vote");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return i;
        }
        @Override
        protected void onPostExecute(Incident result){
            incident = result;
            loc.setText(result.location);
            date.setText(result.date);
            des.setText("Description:\n"+result.description);
            upTxt.setText("+"+result.up_votes);
            downTxt.setText("-"+result.down_votes);
            downBtn.setVisibility(View.VISIBLE);
            upBtn.setVisibility(View.VISIBLE);
        }
    }

    public void voteUp(View view){
           new UpdateVotes().execute(true);
    }

    public void voteDown(View view){
        new UpdateVotes().execute(false);
    }

    public class UpdateVotes extends AsyncTask<Boolean, Void, Void>{

        @Override
        protected Void doInBackground(Boolean... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("ID", String.valueOf(id)));
            params1.add(new BasicNameValuePair("option", String.valueOf(params[0])));
            JSONObject json = jsonParser.makeHttpRequest("http://titansoft.netau.net/update_incident_votes.php",
                    "POST", params1);
            System.out.println(json.toString());
            downBtn.setClickable(false);
            upBtn.setClickable(false);
            new GetIncidentDetails().execute();
            return null;
        }
    }

}
