package rilma.example.com.movieapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import rilma.example.com.movieapp.R;

public class DetailsActivity extends AppCompatActivity {
    @BindView(R.id.movie_title) TextView movieTitle;
    @BindView(R.id.movie_release_date) TextView releaseDate;
    @BindView(R.id.movie_user_rating) TextView userRating;
    @BindView(R.id.movie_overview) TextView synopsis;
    @BindView(R.id.movie_poster_details) ImageView movieThumbnail;
    @BindView(R.id.movie_poster_stretch) ImageView movieStretch;
    @BindView(R.id.favorite_movie_not) ImageView favoriteMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(rilma.example.com.movieapp.R.layout.activity_details);

        ButterKnife.bind(this);

        populateUI();
    }

    private void populateUI() {
        Intent detailsIntent = getIntent();
        final String thumbnail = detailsIntent.getStringExtra("Thumbnail");
        String BASE_URL = "https://image.tmdb.org/t/p/w185/";

        final String title = detailsIntent.getStringExtra("Title");
        final String releaseYear = detailsIntent.getStringExtra("ReleaseDate");
        final String voteAverage = detailsIntent.getStringExtra("UserRating");
        final String overview = detailsIntent.getStringExtra("Synopsis");

        movieTitle.setText(title);
        releaseDate.setText(releaseYear);
        userRating.setText(voteAverage);
        synopsis.setText(overview);
        Picasso.with(DetailsActivity.this).load(BASE_URL + thumbnail).into(movieThumbnail);
        Picasso.with(DetailsActivity.this).load(BASE_URL + thumbnail).into(movieStretch);
        setActionBarTitle(title);
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