package rilma.example.com.movieapp.view;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

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
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rilma.example.com.movieapp.BuildConfig;
import rilma.example.com.movieapp.R;
import rilma.example.com.movieapp.adapter.FavoritesAdapter;
import rilma.example.com.movieapp.adapter.MainAdapter;
import rilma.example.com.movieapp.data.FavoriteContract;
import rilma.example.com.movieapp.model.Movie;

public class MainActivity extends AppCompatActivity {

    private static final String API_KEY = BuildConfig.API_KEY;
    @BindView(R.id.rv_movie_home)
    RecyclerView recyclerView;

    private ProgressDialog progressDialog;
    private Parcelable recyclerViewState;
    private List<Movie> movieItems;
    private AutoFitGridLayoutManager layoutManager;
    private MainAdapter mainAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        progressDialog = new ProgressDialog(this);
        
        recyclerView.setHasFixedSize(true);
        layoutManager = new AutoFitGridLayoutManager(this, 300);
        recyclerView.setLayoutManager(layoutManager);

        requestQueue = Volley.newRequestQueue(this);
        if (!networkStatus(this)) displayErrorMessage();
        else {
            String BASE_URL = "https://api.themoviedb.org/3/movie/popular?&api_key=";
            String url = BASE_URL + API_KEY;
            parseJson(url);
        }
    }

    //Get movie details using volley library and populate recyclerview
    private void parseJson(String moviesUrl) {
        displayProgress("Loading movies...");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, moviesUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            movieItems = new ArrayList<>();

                            //Acquire results object
                            JSONArray resultsArray = response.getJSONArray("results");
                            //Loop through items in resultsArray
                            for (int i = 0; i < resultsArray.length(); i++) {
                                JSONObject resultsObject = resultsArray.getJSONObject(i);
                                //Set movie id value
                                int movieId = resultsObject.getInt("id");
                                //Set movie vote average value
                                String averageVote = resultsObject.getString("vote_average");
                                //Set movie title
                                String movieTitle = resultsObject.getString("title");
                                //Set movie poster path
                                String posterPath = resultsObject.getString("poster_path");
                                //Set movie overview
                                String movieOverview = resultsObject.getString("overview");
                                //Set movie release date
                                String releaseDate = resultsObject.getString("release_date");

                                movieItems.add(new Movie(movieId, averageVote, movieTitle,
                                        movieOverview, releaseDate, posterPath));

                                removeProgress();
                                mainAdapter = new MainAdapter(MainActivity.this, movieItems);
                                recyclerView.setAdapter(mainAdapter);
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
    }

    public void displayProgress(String message){
        progressDialog.show();
        progressDialog.setMessage(message);

    }

    public void removeProgress(){
        progressDialog.dismiss();
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

    private void displayErrorMessage() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage(R.string.no_network);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                R.string.reload_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                        startActivity(getIntent());
                    }
                });

        builder1.setNegativeButton(
                R.string.close_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        MainActivity.this.finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
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
        String language = "&language=en-US";

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort_most_popular) {
            String popularUrl = "https://api.themoviedb.org/3/movie/popular?&api_key=" + API_KEY + language;
            parseJson(popularUrl);
            return true;
        }

        if (id == R.id.sort_top_rated) {
            String topRatedUrl = "https://api.themoviedb.org/3/movie/top_rated?&api_key=" + API_KEY + language;
            parseJson(topRatedUrl);
            return true;
        }

        if (id == R.id.sort_favorites) {
            new FavoriteMoviesFetchTask().execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public class FavoriteMoviesFetchTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            displayProgress("Loading Favorites...");
        }

        @Override
        protected Void doInBackground(Void... items) {
            movieItems = new ArrayList<>();

            Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            assert cursor != null;
            int count = cursor.getCount();
            movieItems = Arrays.asList(new Movie[cursor.getCount()]);
            if (count == 0) {
                return null;
            }
            if (cursor.moveToFirst()) {
                do {
                    int movie_id = cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID));
                    String movie_title = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE));
                    String poster_path = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH));
                    String release_date = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE));
                    String user_rating = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_USER_RATING));
                    String synopsis = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS));

                    movieItems.set(cursor.getPosition(), new Movie(movie_id, user_rating, movie_title, synopsis, release_date, poster_path));
                } while (cursor.moveToNext());
            }

            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            removeProgress();
            FavoritesAdapter favoritesAdapter = new FavoritesAdapter(MainActivity.this, movieItems);
            recyclerView.setAdapter(favoritesAdapter);
        }
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (recyclerView != null) {
            String KEY_RECYCLER_STATE = "recycler_state";
            state.putParcelable(KEY_RECYCLER_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
            recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        }
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        if(state != null)
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (movieItems != null) layoutManager.onRestoreInstanceState((Parcelable) movieItems);
    }
}
