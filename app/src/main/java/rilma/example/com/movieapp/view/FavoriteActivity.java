package rilma.example.com.movieapp.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rilma.example.com.movieapp.R;
import rilma.example.com.movieapp.adapter.FavoritesAdapter;
import rilma.example.com.movieapp.data.FavoriteContract;
import rilma.example.com.movieapp.model.Movie;

public class FavoriteActivity extends AppCompatActivity {
    @BindView(R.id.rv_movie_favorites)
    RecyclerView rvFavorites;

    private List<Movie> favoriteItems;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(rilma.example.com.movieapp.R.layout.activity_favorite);

        ButterKnife.bind(this);
        rvFavorites.setHasFixedSize(true);
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(this, 300);
        rvFavorites.setLayoutManager(layoutManager);

        new FavoriteMoviesFetchTask().execute();
    }

    @SuppressLint("StaticFieldLeak")
    public class FavoriteMoviesFetchTask extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... items) {
            favoriteItems = new ArrayList<>();

            Uri uri = FavoriteContract.FavoriteEntry.CONTENT_URI;
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            assert cursor != null;
            int count = cursor.getCount();
            favoriteItems = Arrays.asList(new Movie[cursor.getCount()]);
            if(count == 0){
                return null;
            }
            if(cursor.moveToFirst()){
                do{
                    int movie_id = cursor.getInt(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_MOVIE_ID));
                    String movie_title = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_TITLE));
                    String poster_path = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_POSTER_PATH));
                    String release_date = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_RELEASE_DATE));
                    String user_rating = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_USER_RATING));
                    String synopsis = cursor.getString(cursor.getColumnIndex(FavoriteContract.FavoriteEntry.COLUMN_SYNOPSIS));

                    favoriteItems.set(cursor.getPosition(), new Movie(movie_id, user_rating, movie_title, synopsis, release_date, poster_path));
                } while (cursor.moveToNext());
            }

            cursor.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            FavoritesAdapter favoritesAdapter = new FavoritesAdapter(FavoriteActivity.this, favoriteItems);
            rvFavorites.setAdapter(favoritesAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
