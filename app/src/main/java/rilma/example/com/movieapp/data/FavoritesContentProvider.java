package rilma.example.com.movieapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

public class FavoritesContentProvider extends ContentProvider {

    public static final int FAVORITES = 100;
    public static final int FAVORITE_WITH_ID = 101;

    //variable for uri matcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //for the whole directory
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_MOVIES, FAVORITES);
        //for a single item
        uriMatcher.addURI(FavoriteContract.AUTHORITY, FavoriteContract.PATH_MOVIES + "/#", FAVORITE_WITH_ID);

        return uriMatcher;
    }

    private FavoritesDbHelper favoritesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        favoritesDbHelper = new FavoritesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = favoritesDbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);
        Cursor cursor;
        switch (match) {
            case FAVORITES:
                break;
            case FAVORITE_WITH_ID:
                String movieId = uri.getPathSegments().get(1);
                selection = "movie_id" + movieId;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        cursor = db.query(FavoriteContract.FavoriteEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        //Get access to database
        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case FAVORITES:
                //Insert values into favorite table
                long id = db.insert(FavoriteContract.FavoriteEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(uri, id);
                } else {
                    throw new SQLException("Failes to insert data into row " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = favoritesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int favoritesDeleted = 0;
        switch (match) {
            case FAVORITES:
                break;
            case FAVORITE_WITH_ID:
                String favorite_id = uri.getPathSegments().get(1);
                favoritesDeleted = db.delete(FavoriteContract.FavoriteEntry.TABLE_NAME, "movie_id=?", new String[]{favorite_id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri : " + uri);
        }

        if (favoritesDeleted != 0) {
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        return favoritesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
