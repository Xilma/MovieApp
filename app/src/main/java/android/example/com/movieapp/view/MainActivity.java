package android.example.com.movieapp.view;

import android.example.com.movieapp.R;
import android.example.com.movieapp.model.Movie;
import android.example.com.movieapp.utils.Api;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Api apiKey;
    private final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?api_key=";
    private String moviesUrl, popularUrl, topRatedUrl;
    private ArrayList<Movie> moviesList, mostPopularList, topRatedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.pb_load);
        apiKey = new Api();
    }

    //AsyncTask to handle network request to moviedb
    public class DisplayMovies extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            moviesUrl = BASE_URL + apiKey.getAPI_KEY();
            popularUrl = "https://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+ apiKey.getAPI_KEY();
            topRatedUrl = "https://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&api_key="+ apiKey.getAPI_KEY();

            moviesList = new ArrayList<>();
            mostPopularList = new ArrayList<>();
            topRatedList = new ArrayList<>();

            return null;
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
        }
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
            Toast.makeText(this, "Sort by most popular", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (id == R.id.sort_top_rated) {
            Toast.makeText(this, "Sort by top rated", Toast.LENGTH_SHORT).show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
