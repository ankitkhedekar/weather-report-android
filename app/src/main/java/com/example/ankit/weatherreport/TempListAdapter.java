package com.example.ankit.weatherreport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ankit on 10/08/15.
 */
public class TempListAdapter extends ArrayAdapter<Temperature> {
    private Context context;
    private Temperature[] temps;
    int resource;
    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public TempListAdapter(Context context, int resource, Temperature[] temps) {
        super(context, resource, temps);

        this.context = context;
        this.temps = temps;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater li = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.listview_item, parent, false);
        ViewHolder holder = new ViewHolder(v);

        Temperature t = temps[position];

        if(t != null){
            holder.temp.setText(t.getTemp());
            holder.timestamp.setText(df.format(new Date(t.getTime())));
        }

        return v;
    }

    static class ViewHolder{

        @Bind(R.id.temp)TextView temp;
        @Bind(R.id.timestamp) TextView timestamp;

        public ViewHolder(View v){
            ButterKnife.bind(this, v);
        }
    }
}
