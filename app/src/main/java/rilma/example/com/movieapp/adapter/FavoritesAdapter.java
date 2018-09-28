package rilma.example.com.movieapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

import rilma.example.com.movieapp.R;
import rilma.example.com.movieapp.model.Movie;
import rilma.example.com.movieapp.view.FavoriteDetailActivity;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
    private Context context;
    private List<Movie> favoriteList;

    public FavoritesAdapter(Context context, List<Movie> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @NonNull
    @Override
    public FavoritesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_favorite_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesAdapter.ViewHolder holder, int position) {
        final Movie moviePoster = favoriteList.get(position);

        String BASE_URL = "https://image.tmdb.org/t/p/w185/";
        Picasso.with(context)
                .load(BASE_URL + moviePoster.getPosterPath())
                .fit()
                .centerCrop()
                .into(holder.movieThumbnail);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent movieDetails = new Intent(context, FavoriteDetailActivity.class);
                movieDetails.putExtra("MovieId", moviePoster.getMovieId());
                movieDetails.putExtra("Thumbnail", moviePoster.getPosterPath());
                movieDetails.putExtra("Title", moviePoster.getTitle());
                movieDetails.putExtra("ReleaseDate", moviePoster.getReleaseDate());
                movieDetails.putExtra("UserRating", moviePoster.getVoteAverage());
                movieDetails.putExtra("Synopsis", moviePoster.getOverview());
                context.startActivity(movieDetails);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView movieThumbnail;
        LinearLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = itemView.findViewById(R.id.movie_thumbnail);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
