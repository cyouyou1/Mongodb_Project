package com.example.mongodbproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mongodbproject.model.Tweet;
import com.example.mongodbproject.parser.TweetJSONParser;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    ListView lv;
    ProgressBar pb;
    List<MyTask> tasks;
    List<Tweet> tweetsList;


    private static final String MONGOLAB_BASE_URL =
            "https://api.mongolab.com/api/1/databases/project_db/collections/simple_tweets/?q={$text:{$search:%22hiking%22}}&l=10&apiKey=zmTpDSixS5MN2Kb6txgHDM9GvxE5sksX";

//    private final static String URL_API_KEY = "zmTpDSixS5MN2Kb6txgHDM9GvxE5sksX";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        lv = (ListView) findViewById(R.id.list_view);
        pb = (ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

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
        if (id == R.id.search) {
            if (isOnline()) {
                requestData(MONGOLAB_BASE_URL);
            }else {
                Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            }
            return true;
        }else if (id == R.id.exit_app ){
            finish();
            return true;
        }else if (id == R.id.map ) {
            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            return true;
        }

        return false;
    }

    private void requestData(String uri) {
        MyTask task = new MyTask();
        task.execute(uri);

    }

    protected void updateDisplay() {
        TweetAdapter adapter = new TweetAdapter(this, R.layout.tweet, tweetsList);
        lv.setAdapter(adapter);
    }


    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private class MyTask extends AsyncTask<String, String, List<Tweet>> {

        @Override
        protected void onPreExecute() {

            if(tasks.size() == 0){
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected List<Tweet> doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            tweetsList = TweetJSONParser.parseFeed(content);

            for (Tweet tweet:tweetsList){
                try {
                    String imageUrl = tweet.getProfile_image_url();
                    if(imageUrl != null){
                        InputStream in = (InputStream) new URL(imageUrl).getContent();
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        tweet.setProfile_pic(bitmap);
                        in.close();
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }

            }
            return tweetsList;


        }

        @Override
        protected void onPostExecute(List<Tweet> result) {

            tasks.remove(this);
            if(tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }
            if (result == null){
                Toast.makeText(MainActivity.this, "Cannot connect to web service", Toast.LENGTH_LONG).show();
                return;
            }

            updateDisplay();

        }



    }
}
