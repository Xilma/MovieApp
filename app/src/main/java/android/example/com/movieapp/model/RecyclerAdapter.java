package android.example.com.movieapp.model;

import android.content.Context;
import android.content.Intent;
import android.example.com.movieapp.R;
import android.example.com.movieapp.view.DetailsActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Movie> movieList;

    public RecyclerAdapter(Context context, List<Movie> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_grid_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Movie moviePoster = movieList.get(position);

        String BASE_URL = "https://image.tmdb.org/t/p/w185/";
        Picasso.with(context)
                .load(BASE_URL + moviePoster.getPosterPath())
                .fit()
                .centerCrop()
                .into(holder.movieThumbnail);

        holder.parentLayout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent movieDetails = new Intent(context, DetailsActivity.class);
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
        return movieList.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView movieThumbnail;
        LinearLayout parentLayout;

        ViewHolder(View itemView) {
            super(itemView);
            movieThumbnail = itemView.findViewById(R.id.movie_thumbnail);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }
}
