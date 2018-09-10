package android.example.com.movieapp.view;

import android.content.Intent;
import android.example.com.movieapp.R;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private TextView movieTitle, releaseDate, userRating, synopsis;
    private ImageView movieThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        movieTitle = findViewById(R.id.movie_title);
        releaseDate = findViewById(R.id.movie_release_date);
        userRating = findViewById(R.id.movie_user_rating);
        synopsis = findViewById(R.id.movie_overview);
        movieThumbnail = findViewById(R.id.movie_poster_details);

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

    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        DetailsActivity.this.finish();
    }
}
