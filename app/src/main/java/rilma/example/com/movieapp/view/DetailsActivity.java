package rilma.example.com.movieapp.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rilma.example.com.movieapp.R;
import rilma.example.com.movieapp.adapter.TrailerAdapter;
import rilma.example.com.movieapp.model.Trailer;

import static rilma.example.com.movieapp.BuildConfig.API_KEY;
import static rilma.example.com.movieapp.view.MainActivity.networkStatus;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.movie_release_date) TextView releaseDate;
    @BindView(R.id.movie_user_rating) TextView userRating;
    @BindView(R.id.movie_overview) TextView synopsis;
    @BindView(R.id.movie_poster_details) ImageView movieThumbnail;
    @BindView(R.id.movie_poster_stretch) ImageView movieStretch;
    @BindView(R.id.favorite_movie_not) ImageView favoriteMovie;
    @BindView(R.id.rv_movie_trailer) RecyclerView rvTrailer;

    private TrailerAdapter trailerAdapter;
    private List<Trailer> trailerClips;
    private RequestQueue requestQueue;
    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(rilma.example.com.movieapp.R.layout.activity_details);

        ButterKnife.bind(this);

        rvTrailer.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        rvTrailer.setLayoutManager(layoutManager);
        
        requestQueue = Volley.newRequestQueue(this);

        if (!networkStatus(this)) displayErrorMessage();
        else populateUI();
    }

    private void populateUI() {
        Intent detailsIntent = getIntent();
        final String thumbnail = detailsIntent.getStringExtra("Thumbnail");
        String BASE_URL = "https://image.tmdb.org/t/p/w185/";

        final String title = detailsIntent.getStringExtra("Title");
        final String releaseYear = detailsIntent.getStringExtra("ReleaseDate");
        final String voteAverage = detailsIntent.getStringExtra("UserRating");
        final String overview = detailsIntent.getStringExtra("Synopsis");
        final int movieId = detailsIntent.getIntExtra("MovieId", 12345);

        movieTitle.setText(title);
        releaseDate.setText(releaseYear);
        userRating.setText(voteAverage);
        synopsis.setText(overview);
        Picasso.with(DetailsActivity.this).load(BASE_URL + thumbnail).into(movieThumbnail);
        Picasso.with(DetailsActivity.this).load(BASE_URL + thumbnail).into(movieStretch);
        setActionBarTitle(title);

        String trailerUrl = MOVIE_BASE_URL + movieId + "/videos?api_key=" + API_KEY + "&language=en-US";
        parseJsonTrailer(trailerUrl);
    }

    //Get movie details using volley library and populate recyclerview
    private void parseJsonTrailer(String moviesUrl) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, moviesUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            trailerClips = new ArrayList<>();

                            //Acquire results object
                            JSONArray resultsArray = response.getJSONArray("results");
                            //Loop through items in resultsArray
                            for (int i = 0; i < resultsArray.length(); i++) {
                                JSONObject resultsObject = resultsArray.getJSONObject(i);
                                //Set trailer name value
                                String trailerName = resultsObject.getString("key");
                                //Set trailer key value
                                String trailerKey = resultsObject.getString("name");

                                trailerClips.add(new Trailer(trailerKey, trailerName));

                                trailerAdapter = new TrailerAdapter(DetailsActivity.this, trailerClips);
                                rvTrailer.setAdapter(trailerAdapter);
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
                        DetailsActivity.this.finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        DetailsActivity.this.finish();
    }
}
