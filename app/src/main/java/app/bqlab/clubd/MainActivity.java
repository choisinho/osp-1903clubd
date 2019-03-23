package app.bqlab.clubd;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //variables
    int score;
    int setSensorNumber;
    int time, maxTime;
    int sensor2Time;
    int sensor3Speed, sensor3Gap;
    boolean racing;
    //objects
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
        setSensor1();
        setSensor2();
        setSensor3();
        setSensor4();
    }

    private void setSensor1() {
        findViewById(R.id.main_sensor1_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서1 설정")
                        .setMessage("만점 기준이 될 시간을 ms 단위로 입력하세요.")
                        .setView(input)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                    maxTime = Integer.parseInt(input.getText().toString());
                                    setSensorNumber += 1;
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor1_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllSet()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("경기 시작")
                            .setMessage("확인 버튼을 누르면 경기 측정이 시작됩니다.")
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("시작", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mDatabase.child("sensor1").child("pass").setValue(1);
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            while (racing) {
                                                time += 1;
                                            }
                                        }
                                    }).start();
                                }
                            }).show();
                } else
                    Toast.makeText(MainActivity.this, "모든 설정을 마쳐야 합니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSensor2() {
        findViewById(R.id.main_sensor2_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서2 설정")
                        .setMessage("만점 기준이 될 시간을 ms 단위로 입력하세요.")
                        .setView(input)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                    sensor2Time = Integer.parseInt(input.getText().toString());
                                    setSensorNumber += 1;
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
    }

    private void setSensor3() {
        findViewById(R.id.main_sensor3_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서3 설정")
                        .setMessage("만점 기준이 될 속도 km/h 단위로 입력하세요.")
                        .setView(input)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                    sensor3Speed = Integer.parseInt(input.getText().toString());
                                    final EditText input2 = new EditText(MainActivity.this);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("센서3 설정")
                                            .setMessage("해당 구간의 간격을 cm 단위로 입력하세요.")
                                            .setView(input2)
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                                    sensor3Gap = Integer.parseInt(input2.getText().toString());
                                                    setSensorNumber += 1;
                                                }
                                            }).show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
    }

    private void setSensor4() {
        findViewById(R.id.main_sensor4_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor4_score)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor4_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor4_score)).setText(scoreAdmit);
                        Toast.makeText(MainActivity.this, "점수가 반영되었습니다.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                }
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

    private int getSpeed(int time, int distance) {
        return distance / time;
    }

    private int getTime(int speed, int distance) {
        return distance / speed;
    }

    private int getDistance(int speed, int time) {
        return speed * time;
    }
}