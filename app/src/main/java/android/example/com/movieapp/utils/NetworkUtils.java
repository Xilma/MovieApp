package android.example.com.movieapp.utils;

import android.example.com.movieapp.model.Movie;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class NetworkUtils {
    private final String RESULTS = "results";
    private final String ID = "id";
    private final String AVERAGE_VOTE = "vote_average";
    private final String TITLE = "title";
    private final String POSTER_PATH = "poster_path";
    private final String ORIGINAL_TITLE = "original_title";
    private final String OVERVIEW = "overview";
    private final String RELEASE_DATE = "release_date";
    private Movie movie;

    //Method to fetch movie data from moviedb
    public ArrayList<Movie> fetchMovie(String url){
        ArrayList<Movie> movies = new ArrayList<Movie>();
        try {
            //Create a url from a String
            URL movieUrl = new URL(url);
            //Open http connection
            HttpURLConnection connection = (HttpURLConnection) movieUrl.openConnection();
            connection.connect();

            //Read the data streams from the moviedb
            InputStream inputStream = connection.getInputStream();
            String movieResults = inputStream.toString();
            //Call parseJson method and pass inputStream results and ArrayList
            parseJson(movieResults, movies);
            //Close input stream
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }

    //Method to parse the json data from moviedb
    public void parseJson(String data, ArrayList<Movie> movieList){
        movie = new Movie();

        try {
            JSONObject jsonObject = new JSONObject(data);
            //Acquire results object
            JSONArray resultsArray = jsonObject.getJSONArray(RESULTS);
            //Loop through items in resultsArray
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultsObject = resultsArray.getJSONObject(i);
                //Set movie id value
                movie.setId(resultsObject.getInt(ID));
                //Set movie vote average value
                movie.setVoteAverage(resultsObject.getDouble(AVERAGE_VOTE));
                //Set movie title
                movie.setTitle(resultsObject.getString(TITLE));
                //Set movie poster path
                movie.setPosterPath(resultsObject.getString(POSTER_PATH));
                //Set movie original title
                movie.setOriginalTitle(resultsObject.getString(ORIGINAL_TITLE));
                //Set movie overview
                movie.setOverview(resultsObject.getString(OVERVIEW));
                //Set movie release date
                movie.setReleaseDate(resultsObject.getString(RELEASE_DATE));

                //Add the movie object to the array list
                movieList.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "An error occurred while parsing JSON object", e);
        }
    }
}
