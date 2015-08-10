package com.example.ankit.weatherreport;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import android.net.http.AndroidHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import butterknife.Bind;
import butterknife.ButterKnife;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;


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
        setup();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        if (id == R.id.action_refresh) {
            setup();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setup(){

        new LoadHistory().execute();
        new GetTemp().execute();

    }

    /* Used only while testing*/
    private String getCurrentTemp(){
        return (new Random().nextInt(11) + 20) + "°C";
    }


    class LoadHistory extends AsyncTask<Void, Void, List<Temperature>>{
        @Override
        protected List<Temperature> doInBackground(Void... params) {
            List<Temperature> history = Temperature.findWithQuery(Temperature.class, "Select * from Temperature order by ts desc");;
            return history;
        }

        @Override
        protected void onPostExecute(List<Temperature> temperatures) {
            TempListAdapter adapter = new TempListAdapter(MainActivity.this,R.layout.listview_item,temperatures.toArray(new Temperature[temperatures.size()]));
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

    class GetTemp extends AsyncTask<Void, Void, String>{
        private static final String URL = "http://api.worldweatheronline.com/free/v2/weather.ashx?q=Mumbai&format=json&key=7e54abfe1cd41da95e72db91b4eb7";
        AndroidHttpClient httpClient = AndroidHttpClient.newInstance("");
        ProgressDialog progress = new ProgressDialog(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progress.setMessage("Loading");
            progress.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            HttpGet request = new HttpGet(URL);
            JSONResponseHandler responseHandler = new JSONResponseHandler();
            try{
                return httpClient.execute(request, responseHandler);
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            currentTemp = s + "°C";
            currentTs = System.currentTimeMillis();
            nowTemp.setText(currentTemp);

            Temperature t = new Temperature(currentTemp, currentTs);
            new SaveToHistory().execute(t);
            progress.dismiss();
            super.onPostExecute(s);
        }
    }

    private class JSONResponseHandler implements ResponseHandler<String> {
        @Override
        public String handleResponse(HttpResponse httpResponse) throws IOException {
            String JSONResponse = new BasicResponseHandler() .handleResponse(httpResponse);
            try{
                JSONObject responseObject = (JSONObject) new JSONTokener(JSONResponse).nextValue();

                JSONObject dataObject = responseObject.getJSONObject("data");
                JSONArray currentObject = dataObject.getJSONArray("current_condition");
                String temp = ((JSONObject)currentObject.get(0)).get("temp_C").toString();
                return temp;
            } catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

}
