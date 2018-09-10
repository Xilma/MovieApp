package android.example.com.movieapp.model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String voteAverage;
    private String title;
    private String overview;
    private String releaseDate;
    private String posterPath;

    public Movie() {
    }

    public Movie(String voteAverage, String title, String overview, String releaseDate, String posterPath) {
        this.voteAverage = voteAverage;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.posterPath = posterPath;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }
}
