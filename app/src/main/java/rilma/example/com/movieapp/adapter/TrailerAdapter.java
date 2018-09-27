package rilma.example.com.movieapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rilma.example.com.movieapp.R;
import rilma.example.com.movieapp.model.Trailer;

import static rilma.example.com.movieapp.view.DetailsActivity.YOUTUBE_BASE_URL;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.ViewHolder>{
    private Context context;
    private List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList){
        this.context = context;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_trailer_item, parent, false);
        return new ViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.ViewHolder holder, int position) {
        final Trailer trailer = trailerList.get(position);

        holder.trailerTitle.setText(trailer.getTrailerName());
        final String YOUTUBE_URL = YOUTUBE_BASE_URL + trailer.getTrailerKey();

        holder.trailerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Explicit intent to open in youtube app
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.google.android.youtube");
                context.startActivity(intent);
            }
        });

        holder.shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, YOUTUBE_URL);
                context.startActivity(Intent.createChooser(sharingIntent, "Share with..."));
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.trailer_title)
        TextView trailerTitle;
        @BindView(R.id.share_movie)
        ImageView shareIcon;

        ViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
