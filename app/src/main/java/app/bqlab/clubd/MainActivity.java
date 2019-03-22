package app.bqlab.clubd;

import android.app.Service;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InternetCheck.showDialogAfterCheck(this);
        checkService();
        init();
    }

    private void init() {

    }

    private void checkService() {
        if (!ServiceCheck.isRunning(this, FirebaseService.class.getName())) {
            startService(new Intent(this, FirebaseService.class));
        }
    }
}