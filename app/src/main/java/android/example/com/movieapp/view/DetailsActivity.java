package android.example.com.movieapp.view;

import android.example.com.movieapp.R;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        DetailsActivity.this.finish();
    }
}
