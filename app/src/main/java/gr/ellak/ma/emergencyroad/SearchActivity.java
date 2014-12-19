package gr.ellak.ma.emergencyroad;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Billys on 18/12/2014.
 */
public class SearchActivity extends ActionBarActivity {
    Activity act = this;
    JSONParser jsonParser = new JSONParser();
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Incident");

        list = (ListView) findViewById(R.id.search_list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }
    public void search(View view){
        new GetIncident().execute();

    }
    public class GetIncident extends AsyncTask<Void,Void,List<Incident>>{

        @Override
        protected List<Incident> doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("location", ((EditText) act.findViewById(R.id.search_etxt)).getText().toString()));
            JSONObject json = jsonParser.makeHttpRequest("http://titansoft.netau.net/get_incidents.php",
                    "GET", params1);
            List<Incident> list = new ArrayList<>();
            try {
                JSONArray objs = json.getJSONArray("incident");

                for(int i = 0; i < objs.length(); i++){
                    Incident inc = new Incident();
                    inc.location = objs.getJSONObject(i).getString("location");
                    inc.date = objs.getJSONObject(i).getString("date");
                    inc.ID = objs.getJSONObject(i).getInt("ID");
                    list.add(inc);
                }
                System.out.println("asdad");
            } catch (JSONException e) {

                e.printStackTrace();
            }
            return list;
        }

        @Override
        protected void onPostExecute(final List<Incident> results){
            list.setAdapter(new SearchListAdapter(act, results));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(act, IncidentActivity.class);
                    Bundle extra = new Bundle();
                    extra.putInt("id", results.get(position).ID);
                    extra.putString("title", results.get(position).location);
                    i.putExtras(extra);
                    startActivity(i);
                }
            });
        }
    }
}
