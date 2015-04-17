package com.example.mongodbproject.parser;

import com.example.mongodbproject.model.Tweet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TweetJSONParser {


        public static List<Tweet> parseFeed(String content) {

            try {
                JSONArray ar = new JSONArray(content);
                List<Tweet> tweetsList = new ArrayList<>();

                for (int i = 0; i < ar.length(); i++) {
                    JSONObject object = ar.getJSONObject(i);
                    Tweet tweet = new Tweet();

                    tweet.setTweet_text(object.getString("tweet_text"));
                    tweet.setCreated_at(object.getString("created_at"));
                    tweet.setProfile_image_url(object.getString("profile_image_url"));

                    tweetsList.add(tweet);
                }

                return tweetsList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
}

