package com.example.ankit.weatherreport;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;



public class MainActivity extends ActionBarActivity {
    @Bind(R.id.nowTemp) TextView nowTemp;
    @Bind(R.id.listView) ListView lv;
    String currentTemp;
    Long currentTs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        new LoadHistory().execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentTemp = getCurrentTemp();
        currentTs = System.currentTimeMillis();
        nowTemp.setText(currentTemp);

        Temperature t = new Temperature(currentTemp, currentTs);
        new SaveToHistory().execute(t);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private String getCurrentTemp(){
        return (new Random().nextInt(11) + 20) + "°C";
    }


    class LoadHistory extends AsyncTask<Void, Void, List<Temperature>>{
        @Override
        protected List<Temperature> doInBackground(Void... params) {
            List<Temperature> history = Temperature.listAll(Temperature.class);
            return history;
        }

        @Override
        protected void onPostExecute(List<Temperature> temperatures) {
            TempListAdapter adapter = new TempListAdapter(getApplicationContext(),R.layout.listview_item,temperatures.toArray(new Temperature[temperatures.size()]));
            lv.setAdapter(adapter);
        }
    }

    class SaveToHistory extends AsyncTask<Temperature, Void, Void>{

        @Override
        protected Void doInBackground(Temperature... params) {
            params[0].save();
            return null;
        }
    }
}
