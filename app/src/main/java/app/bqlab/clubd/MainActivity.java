package app.bqlab.clubd;

import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //variables
    int setSensorNumber;
    int time, maxTime;
    //
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InternetCheck.showDialogAfterCheck(this);
        setupFirebase();
        init();
    }

    private void init() {

    }

    private void setSensor1() {
        findViewById(R.id.main_sensor1_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setHint("ms 단위로 시간을 입력하세요.");
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서1 설정")
                        .setMessage("만점 기준이 될 시간을 입력하세요.")
                        .setView(input)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                maxTime = Integer.parseInt(input.getText().toString());
                                setSensorNumber += 1;
                            }
                        }).show();

            }
        });
        findViewById(R.id.main_sensor1_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllSet())
                    mDatabase.child("sensor1").child("pass").setValue(1);
                else
                    Toast.makeText(MainActivity.this, "모든 설정을 마쳐야 합니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupFirebase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isAllSet() {
        return setSensorNumber == 18;
    }
}