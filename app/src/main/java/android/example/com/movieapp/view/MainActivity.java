package android.example.com.movieapp.view;

import android.content.Context;
import android.example.com.movieapp.R;
import android.example.com.movieapp.model.Movie;
import android.example.com.movieapp.model.RecyclerAdapter;
import android.example.com.movieapp.utils.ApiUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ApiUtils apiUtilsKey;
    private RecyclerView recyclerView;
    private List<Movie> movieItems;
    private RecyclerAdapter recyclerAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiUtilsKey = new ApiUtils();

        recyclerView = findViewById(R.id.rv_movie_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        movieItems = new ArrayList<>();

        requestQueue = Volley.newRequestQueue(this);
        String BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=";
        String url = BASE_URL + apiUtilsKey.getAPI_KEY();
        parseJson(url);
    }

    //Get movie details using volley library and populate recyclerview
    private void parseJson(String moviesUrl) {
        if (networkStatus(MainActivity.this)) {
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, moviesUrl, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                //Acquire results object
                                JSONArray resultsArray = response.getJSONArray("results");
                                //Loop through items in resultsArray
                                for (int i = 0; i < resultsArray.length(); i++) {
                                    JSONObject resultsObject = resultsArray.getJSONObject(i);
                                    //Set movie id value
                                    int movieId = resultsObject.getInt("id");
                                    //Set movie vote average value
                                    double averageVote = resultsObject.getDouble("vote_average");
                                    //Set movie title
                                    String movieTitle = resultsObject.getString("title");
                                    //Set movie poster path
                                    String posterPath = resultsObject.getString("poster_path");
                                    //Set movie original title
                                    String originalTitle = resultsObject.getString("original_title");
                                    //Set movie overview
                                    String movieOverview = resultsObject.getString("overview");
                                    //Set movie release date
                                    String releaseDate = resultsObject.getString("release_date");

                                    movieItems.add(new Movie(movieId, averageVote, originalTitle, movieTitle,
                                            movieOverview, releaseDate, posterPath));

                                    recyclerAdapter = new RecyclerAdapter(MainActivity.this, movieItems);
                                    recyclerView.setAdapter(recyclerAdapter);
                                    recyclerAdapter.notifyDataSetChanged();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });
            requestQueue.add(request);

        } else {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    //Method to check if device is connected to internet or not
    public static Boolean networkStatus(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Find the menuItem to add your SubMenu
        MenuItem myMenuItem = menu.findItem(R.id.action_sort);

        getMenuInflater().inflate(R.menu.sub_menu, myMenuItem.getSubMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_most_popular) {
            String popularUrl = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + apiUtilsKey.getAPI_KEY();
            parseJson(popularUrl);
            Toast.makeText(this, "Sort by popularity", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.sort_top_rated) {
            String topRatedUrl = "https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key=" + apiUtilsKey.getAPI_KEY();
            parseJson(topRatedUrl);
            Toast.makeText(this, "Sort by top rated", Toast.LENGTH_SHORT).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
