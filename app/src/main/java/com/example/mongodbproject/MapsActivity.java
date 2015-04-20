package com.example.mongodbproject;



import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;



import com.example.mongodbproject.model.Tweet;
import com.example.mongodbproject.parser.TweetJSONParser;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.


    private static final String MONGOLAB_HIKING_URL =
            "https://api.mongolab.com/api/1/databases/project_db/collections/simple_tweets/?q={$text:{$search:%22reading%20book%22}}&l=10&apiKey=zmTpDSixS5MN2Kb6txgHDM9GvxE5sksX";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        AddMarkerTask task = new AddMarkerTask();
        task.execute(MONGOLAB_HIKING_URL);
    }


    private class AddMarkerTask extends AsyncTask<String, String, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(String... params) {

            String content = HttpManager.getData(params[0]);
            List<Tweet> tweetsList = TweetJSONParser.parseFeed(content);
            List<LatLng> latLngList = new ArrayList<>();

            for (Tweet tweet:tweetsList){

                try {
                    LatLng markerLatLng = tweet.getLatLng();
                    if(markerLatLng != null){
                        latLngList.add(markerLatLng);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return latLngList;


        }

        @Override
        protected void onPostExecute(List<LatLng> latLngList) {

            for (LatLng latLng: latLngList){
                mMap.addMarker(new MarkerOptions().position(latLng).title("Tweet"));
            }
        }


        }

    }
