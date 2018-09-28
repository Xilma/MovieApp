package rilma.example.com.movieapp.view;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import butterknife.OnClick;
import rilma.example.com.movieapp.R;
import rilma.example.com.movieapp.adapter.ReviewsAdapter;
import rilma.example.com.movieapp.adapter.TrailerAdapter;
import rilma.example.com.movieapp.data.FavoriteContract;
import rilma.example.com.movieapp.model.Review;
import rilma.example.com.movieapp.model.Trailer;

import static rilma.example.com.movieapp.BuildConfig.API_KEY;
import static rilma.example.com.movieapp.view.MainActivity.networkStatus;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.movie_title)
    TextView movieTitle;
    @BindView(R.id.movie_release_date)
    TextView releaseDate;
    @BindView(R.id.movie_user_rating)
    TextView userRating;
    @BindView(R.id.movie_overview)
    TextView synopsis;
    @BindView(R.id.movie_poster_details)
    ImageView movieThumbnail;
    @BindView(R.id.movie_poster_stretch)
    ImageView movieStretch;
    @BindView(R.id.favorite_movie_not)
    ImageView favoriteMovieNot;
    @BindView(R.id.favorite_movie_yes)
    ImageView favoriteMovieYes;
    @BindView(R.id.rv_movie_trailer)
    RecyclerView rvTrailer;
    @BindView(R.id.rv_movie_reviews)
    RecyclerView rvReviews;

    private TrailerAdapter trailerAdapter;
    private List<Trailer> trailerClips;

    private ReviewsAdapter reviewsAdapter;
    private List<Review> reviewList;

    private RequestQueue requestQueue;

    public static int MOVIE_ID;
    public static String POSTER_PATH;
    public static final String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(rilma.example.com.movieapp.R.layout.activity_details);

        ButterKnife.bind(this);

        rvTrailer.setHasFixedSize(true);
        rvTrailer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        rvReviews.setHasFixedSize(true);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));

        requestQueue = Volley.newRequestQueue(this);

        Intent details = getIntent();
        MOVIE_ID = details.getIntExtra("MovieId", 12345);
        POSTER_PATH = details.getStringExtra("Thumbnail");

        if (!networkStatus(this)) displayErrorMessage();
        else {
            checkFavorite();
            populateUI();
        }
    }

    private void populateUI() {
        Intent detailsIntent = getIntent();
        String BASE_URL = "https://image.tmdb.org/t/p/w185/";

        final String title = detailsIntent.getStringExtra("Title");
        final String releaseYear = detailsIntent.getStringExtra("ReleaseDate");
        final String voteAverage = detailsIntent.getStringExtra("UserRating");
        final String overview = detailsIntent.getStringExtra("Synopsis");

        movieTitle.setText(title);
        releaseDate.setText(releaseYear);
        userRating.setText(voteAverage);
        synopsis.setText(overview);
        Picasso.with(DetailsActivity.this).load(BASE_URL + POSTER_PATH).into(movieThumbnail);
        Picasso.with(DetailsActivity.this).load(BASE_URL + POSTER_PATH).into(movieStretch);
        setActionBarTitle(title);

        String trailerUrl = MOVIE_BASE_URL + MOVIE_ID + "/videos?api_key=" + API_KEY + "&language=en-US";
        String reviewUrl = MOVIE_BASE_URL + MOVIE_ID + "/reviews?api_key=" + API_KEY + "&language=en-US";
        parseJsonTrailer(trailerUrl);
        parseJsonReview(reviewUrl);
    }

    private void checkFavorite() {
        String id = Integer.toString(MOVIE_ID);
        String movieID = "/" + id;
        Uri uri = Uri.parse(FavoriteContract.FavoriteEntry.CONTENT_URI + movieID);
        Cursor cursor = getContentResolver().query(uri,
                null,
                null,
                null,
                null);
        assert cursor != null;
        if(!cursor.moveToNext()){
            favoriteMovieNot.setVisibility(View.VISIBLE);
            favoriteMovieYes.setVisibility(View.GONE);
        } else{
            favoriteMovieYes.setVisibility(View.VISIBLE);
            favoriteMovieNot.setVisibility(View.GONE);
        }

        cursor.close();
    }

    //Add to database when grey favorite icon is clicked
    @OnClick(R.id.favorite_movie_not)
    public void addToDatabase() {
        String favoriteTitle = movieTitle.getText().toString();
        String favoriteReleaseDate = releaseDate.getText().toString();
        String favoriteUserRating = userRating.getText().toString();
        String favoriteSynopsis = synopsis.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_TITLE, favoriteTitle);
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH, POSTER_PATH);
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE, favoriteReleaseDate);
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_USER_RATING, favoriteUserRating);
        contentValues.put(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS, favoriteSynopsis);

        Uri uri = getContentResolver().insert(FavoriteContract.FavoriteEntry.CONTENT_URI, contentValues);
        if(uri != null){
            Toast.makeText(DetailsActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show();

            favoriteMovieNot.setVisibility(View.GONE);
            favoriteMovieYes.setVisibility(View.VISIBLE);
        }
    }

    //Delete from database when yellow favorite icon is clicked
    @OnClick(R.id.favorite_movie_yes)
    public void deleteFromDatabase(){
        String favoriteId = Integer.toString(MOVIE_ID);
        Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(favoriteId).build();
        int favoriteDeleted = getContentResolver().delete(uri, null, null);
        if(favoriteDeleted == 1){
            Toast.makeText(DetailsActivity.this, "Deleted from Favorites", Toast.LENGTH_SHORT).show();
        }
        favoriteMovieNot.setVisibility(View.VISIBLE);
        favoriteMovieYes.setVisibility(View.GONE);
    }

    //Get trailer details using volley library and populate recyclerview
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
                                String trailerName = resultsObject.getString("name");
                                //Set trailer key value
                                String trailerKey = resultsObject.getString("key");

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

    //Get review details using volley library and populate recyclerview
    private void parseJsonReview(String moviesUrl) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, moviesUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            reviewList = new ArrayList<>();

                            //Acquire results object
                            JSONArray resultsArray = response.getJSONArray("results");
                            //Loop through items in resultsArray
                            for (int i = 0; i < resultsArray.length(); i++) {
                                JSONObject resultsObject = resultsArray.getJSONObject(i);
                                //Get review author value
                                String author = resultsObject.getString("author");
                                //Get review content value
                                String content = resultsObject.getString("content");

                                reviewList.add(new Review(author, content));

                                reviewsAdapter = new ReviewsAdapter(DetailsActivity.this, reviewList);
                                rvReviews.setAdapter(reviewsAdapter);
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
    }
}
