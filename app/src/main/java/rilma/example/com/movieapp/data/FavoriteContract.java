package rilma.example.com.movieapp.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class FavoriteContract {

    public static final String AUTHORITY = "rilma.example.com.movieapp";
    //The base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    //Paths for accessing data
    public static final String PATH_MOVIES = "favorites";

    public static final class FavoriteEntry implements BaseColumns{
        //content uri
        public static final Uri CONTENT_URI = BASE_CONTENT_URI
                .buildUpon()
                .appendPath(PATH_MOVIES)
                .build();

        //Favorite table and column names
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_SYNOPSIS = "synopsis";
    }
}
