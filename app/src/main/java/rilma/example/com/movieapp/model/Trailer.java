package rilma.example.com.movieapp.model;

public class Trailer {
    String trailerKey;
    String trailerName;

    public Trailer(String trailerKey, String trailerName) {
        this.trailerKey = trailerKey;
        this.trailerName = trailerName;
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }
}
