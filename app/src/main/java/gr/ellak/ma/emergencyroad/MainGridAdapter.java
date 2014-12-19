package gr.ellak.ma.emergencyroad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Billys on 17/12/2014.
 */
public class MainGridAdapter extends BaseAdapter {
    Context con;
    static LayoutInflater inflater=null;

    public MainGridAdapter(Context con){
        this.con = con;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) con
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(convertView == null)
            convertView = inflater.inflate(R.layout.main_grid_item, parent, false);

        TextView txt = (TextView) convertView.findViewById(R.id.menu_txt);
        ImageView icon = (ImageView) convertView.findViewById(R.id.menu_img);

        if(position == 0){
            txt.setText("Submit Incident");
            icon.setImageResource(R.drawable.ic_action_post);
        }
        else if(position == 1){
            txt.setText("Find Incidents Nearby");
            icon.setImageResource(R.drawable.ic_action_nearby);
        }
        else if(position == 2){
            txt.setText("Search Incidents");
            icon.setImageResource(R.drawable.ic_action_search);
        }
        else if(position == 3){
            txt.setText("Info");
            icon.setImageResource(R.drawable.ic_action_info);
        }

        return convertView;
    }
}
