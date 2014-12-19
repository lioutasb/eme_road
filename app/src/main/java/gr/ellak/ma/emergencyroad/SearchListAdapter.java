package gr.ellak.ma.emergencyroad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Billys on 18/12/2014.
 */
public class SearchListAdapter extends BaseAdapter {

    Context con;
    List<Incident> list;
    static LayoutInflater inflater=null;

    public SearchListAdapter(Context con, List<Incident> list){
        this.con = con;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            convertView = inflater.inflate(R.layout.search_result_item, parent, false);

        TextView location = (TextView) convertView.findViewById(R.id.loc_title);
        TextView date = (TextView) convertView.findViewById(R.id.date_txt);

        location.setText(list.get(position).location);
        date.setText(list.get(position).date);
        return convertView;
    }
}
