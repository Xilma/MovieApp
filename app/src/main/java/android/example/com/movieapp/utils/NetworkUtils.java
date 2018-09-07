package android.example.com.movieapp.utils;

import android.content.Context;
import android.example.com.movieapp.model.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.content.ContentValues.TAG;

public class NetworkUtils {

    //Method to parse the json data from moviedb
    public static void parseJson(String data){
        Movie movie = new Movie();

        try {
            JSONObject jsonObject = new JSONObject(data);
            //Acquire results object
            JSONArray resultsArray = jsonObject.getJSONArray("results");
            //Loop through items in resultsArray
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultsObject = resultsArray.getJSONObject(i);
                //Set movie id value
                movie.setId(resultsObject.getInt("id"));
                //Set movie vote average value
                movie.setVoteAverage(resultsObject.getDouble("vote_average"));
                //Set movie title
                movie.setTitle(resultsObject.getString("title"));
                //Set movie poster path
                movie.setPosterPath(resultsObject.getString("poster_path"));
                //Set movie original title
                movie.setOriginalTitle(resultsObject.getString("original_title"));
                //Set movie overview
                movie.setOverview(resultsObject.getString("overview"));
                //Set movie release date
                movie.setReleaseDate(resultsObject.getString("release_date"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "An error occurred while parsing JSON object", e);
        }
    }

    //Method to check if device is connected to internet or not
    public static Boolean networkStatus(Context context){
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnected();
    }
}
