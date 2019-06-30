package ca.weizhi.fokotest;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


// this activity display the country name and country flag after the player item clicked
public class PlayerCountryActivity extends AppCompatActivity {

    private String playerId;

    private String countryCode;

    private String countryName;

    private String flagUrl;

    private ImageView flagView;

    private TextView countryView;

    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player_country);

        init();

        // generate a new playerUrl with the player id
        String playerUrl = "https://statsapi.web.nhl.com/api/v1/people/"+playerId;

        // get the string of player information from playerURl in the background
        // after get the string from playerUrl
        // get the nationality information of this player
        new ReadPlayerJsonTask().execute(playerUrl);

    }

    private void init(){
        Toolbar toolbar = findViewById(R.id.toolbar);

        flagView=findViewById(R.id.flag);

        countryView=findViewById(R.id.country_name);

        backButton=findViewById(R.id.back);

        setSupportActionBar(toolbar);

        TextView textView =findViewById(R.id.country_name);

        textView.setText(getIntent().getStringExtra("id"));

        Log.i("id",""+getIntent().getStringExtra("id"));

        playerId =  getIntent().getStringExtra("id");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

    }


    // get the string of player information from playerURl in the background
    // after get the string from playerUrl
    // get the nationality information of this player
    private class ReadPlayerJsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {

            super.onPreExecute();

        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);

                connection = (HttpURLConnection) url.openConnection();

                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // after getting the string
            // use the getCountryCode function to get the country code from string
            countryCode = getCountryCode(result);

            Log.i("country name",countryCode);


            // if the countryCode is ok, then generate a new countryUrl with the country code
            // read the string of country information fro the country url in the background
            // after getting the string from url
            // get the country name and flag url
            if (countryCode!=null){

                String countyUrl="https://restcountries.eu/rest/v2/alpha/"+countryCode;

                new ReadCountryJsonTask().execute(countyUrl);

            }
        }
    }


    //get the country code from string
    private String getCountryCode(String jsonString){

        String nationality=null;

        if(jsonString!=null){

            try {

                JSONObject rawObj = new JSONObject(jsonString);

                JSONArray playerJsonArray= rawObj.getJSONArray("people");

                JSONObject playerJsonObject =  playerJsonArray.getJSONObject(0);

                 nationality = playerJsonObject.getString("nationality");

                Log.i("nation nality",nationality);

                return nationality;

            } catch (Throwable t) {

                Log.e("PlayerCountryActivity", t.toString());
            }

        }

        return  nationality;

    }


    // read the string of country information fro the country url in the background
    // after getting the string from url
    // get the country name and flag url
    private class ReadCountryJsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... params) {

            HttpURLConnection connection = null;

            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }



        // after getting the string from url
        // get the country name and flag url
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // get the flag url and country name from the string
            // using the getCountryFlagName func
            getCountryFlagName(result);


            // if flag url and country name are ok
            // put the country name on the page and load the image from the url
            if(flagUrl!=null&& countryName!=null) {

                Utils.fetchSvg(PlayerCountryActivity.this, flagUrl, flagView);

                countryView.setText(countryName);

                flagView.setVisibility(View.VISIBLE);
                countryView.setVisibility(View.VISIBLE);

            }

        }
    }

    // get the flag url and country name from the string
    private void getCountryFlagName(String jsonString){
        if(jsonString!=null){


            try {

                JSONObject rawObj = new JSONObject(jsonString);

                countryName=rawObj.getString("name");

                flagUrl=rawObj.getString("flag");


            } catch (Throwable t) {
                Log.e("PlayerCountryActivity", t.toString());
            }

        }

    }



}
