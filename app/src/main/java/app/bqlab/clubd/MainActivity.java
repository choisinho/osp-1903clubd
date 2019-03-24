package app.bqlab.clubd;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    //variables
    boolean racing;
    long totalTime, totalScore;
    long setSensorNumber;
    long sensor1SetTime;
    long sensor2SetTime, sensor2Time;
    long sensor3SetSpeed, sensor3SetGap, sensor3Time, sensor3Distance;
    long sensor4Distance;
    long sensor5SetSpeed, sensor5SetGap, sensor5Time, sensor5Distance;
    long sensor6Distance;
    long sensor8SetTime, sensor8Time;
    long sensor9SetSpeed, sensor9SetGap, sensor9Time, sensor9Distance;
    long sensor10Distance;
    long sensor12SetTime, sensor12Range, sensor12Time;
    long sensor14SetSpeed, sensor14SetGap;
    long sensor15SetSpeed, sensor15SetGap;
    long sensor18Time;
    long lastPassedSensor;
    //objects
    DatabaseReference mDatabase;
    DataSnapshot mSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InternetCheck.showDialogAfterCheck(this);
        setupDatabase();
        init();
    }

    private void init() {
        setSensor1();
        setSensor2();
        setSensor3();
        setSensor4();
        setSensor5();
        setSensor6();
        setSensor7();
        setSensor8();
        setSensor9();
        setSensor10();
        setSensor11();
        setSensor12();
        setSensor13();
        setSensor14();
        setSensor15();
        setSensor16();
        setSensor17();
        setSensor18();
    }

    private void setupDatabase() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSnapshot = dataSnapshot;
                if (isSensorPassed("sensor1")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (racing) {
                                totalTime += 1;
                            }
                        }
                    }).start();
                    lastPassedSensor = 1;
                } else if (isSensorPassed("sensor2")) {
                    sensor2Time = totalTime;
                    ((TextView) findViewById(R.id.main_sensor2_pass)).setText("O");
                    lastPassedSensor = 2;
                } else if (isSensorPassed("sensor3")) {
                    sensor3Time = totalTime;
                    sensor3Distance = (int) getDatabase("sensor3").child("distance").getValue();
                    String text = String.valueOf(sensor3Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor3_distance)).setText(text);
                    lastPassedSensor = 3;
                } else if (isSensorPassed("sensor4")) {
                    //calculate distance, time, speed
                    long score = 0;
                    long time = totalTime - sensor3Time;
                    double speed = ((double) sensor3SetGap / 100000d) / ((double) time / 1000000d);
                    sensor4Distance = (int) getDatabase("sensor4").child("distance").getValue();
                    //calculate score
                    if (sensor4Distance < 10)
                        score += 7;
                    else if (sensor4Distance < 15)
                        score += 6;
                    else if (sensor4Distance < 20)
                        score += 5;
                    else
                        score += 1;
                    if (speed < (sensor3SetSpeed / 2d)) {
                        double speedGap = (sensor3SetSpeed / 2d) - speed;
                        score -= (long) (speedGap / 2d);
                    } else if (speed > (sensor3SetSpeed / 2d)) {
                        double speedGap = speed - (sensor3SetSpeed / 2d);
                        score += (long) (speedGap / 2d);
                    }
                    if (score <= 0)
                        score = 1;
                    //visualize speed, distance, score
                    String speedText = String.valueOf(speed) + "km/h";
                    String distanceText = String.valueOf(sensor4Distance) + "cm";
                    String scoreText = String.valueOf(score);
                    ((TextView) findViewById(R.id.main_sensor4_speed)).setText(speedText);
                    ((TextView) findViewById(R.id.main_sensor4_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor4_score)).setText(scoreText);
                    //finish
                    lastPassedSensor = 4;
                } else if (isSensorPassed("sensor5")) {
                    sensor5Time = totalTime;
                    sensor5Distance = (int) getDatabase("sensor5").child("distance").getValue();
                    String text = String.valueOf(sensor5Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor5_distance)).setText(text);
                    lastPassedSensor = 5;
                } else if (isSensorPassed("sensor6")) {
                    //calculate distance, time, speed
                    long score = 0;
                    long time = totalTime - sensor5Time;
                    double speed = ((double) sensor5SetGap / 100000d) / ((double) time / 1000000d);
                    sensor6Distance = (int) getDatabase("sensor6").child("distance").getValue();
                    //calculate score
                    if (sensor6Distance < 10)
                        score += 7;
                    else if (sensor6Distance < 15)
                        score += 6;
                    else if (sensor6Distance < 20)
                        score += 5;
                    else
                        score += 1;
                    if (speed < (sensor5SetSpeed / 2d)) {
                        double speedGap = (sensor5SetSpeed / 2d) - speed;
                        score -= (long) (speedGap / 2d);
                    } else if (speed > (sensor5SetSpeed / 2d)) {
                        double speedGap = speed - (sensor5SetSpeed / 2d);
                        score += (long) (speedGap / 2d);
                    }
                    if (score <= 0)
                        score = 1;
                    //visualize speed, distance, score
                    String speedText = String.valueOf(speed) + "km/h";
                    String distanceText = String.valueOf(sensor6Distance) + "cm";
                    String scoreText = String.valueOf(score);
                    ((TextView) findViewById(R.id.main_sensor6_speed)).setText(speedText);
                    ((TextView) findViewById(R.id.main_sensor6_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor6_score)).setText(scoreText);
                    //finish
                    lastPassedSensor = 6;
                } else if (isSensorPassed("sensor7")) {
                    //set about time and pass
                    long time = totalTime - sensor2Time;
                    String timeText = String.valueOf(time) + "ms";
                    ((TextView) findViewById(R.id.main_sensor7_time)).setText(timeText);
                    ((TextView) findViewById(R.id.main_sensor7_pass)).setText("O");
                    // set about score
                    long score = 0;
                    if (time < (sensor2SetTime / 2)) {
                        long timeGap = (sensor2SetTime / 2) - time;
                        score -= timeGap / 500;
                    } else if (time > (sensor2SetTime / 2)) {
                        long timeGap = time - (sensor2SetTime / 2);
                        score += timeGap / 500;
                    }
                    ((TextView) findViewById(R.id.main_sensor7_score)).setText(String.valueOf(score));
                    lastPassedSensor = 7;
                } else if (isSensorPassed("sensor8")) {
                    sensor8Time = totalTime;
                    ((TextView) findViewById(R.id.main_sensor8_pass)).setText("O");
                    lastPassedSensor = 8;
                } else if (isSensorPassed("sensor9")) {
                    sensor9Time = totalTime;
                    sensor9Distance = (int) getDatabase("sensor9").child("distance").getValue();
                    String text = String.valueOf(sensor9Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor3_distance)).setText(text);
                    lastPassedSensor = 9;
                } else if (isSensorPassed("sensor10")) {
                    //calculate distance, time, speed
                    long score = 0;
                    long time = totalTime - sensor9Time;
                    double speed = ((double) sensor9SetGap / 100000d) / ((double) time / 1000000d);
                    sensor10Distance = (int) getDatabase("sensor10").child("distance").getValue();
                    //calculate score
                    if (sensor10Distance < 10)
                        score += 7;
                    else if (sensor10Distance < 15)
                        score += 6;
                    else if (sensor10Distance < 20)
                        score += 5;
                    else
                        score += 1;
                    if (speed < (sensor9SetSpeed / 2d)) {
                        double speedGap = (sensor9SetSpeed / 2d) - speed;
                        score -= (long) (speedGap / 2d);
                    } else if (speed > (sensor9SetSpeed / 2d)) {
                        double speedGap = speed - (sensor9SetSpeed / 2d);
                        score += (long) (speedGap / 2d);
                    }
                    if (score <= 0)
                        score = 1;
                    //visualize speed, distance, score
                    String speedText = String.valueOf(speed) + "km/h";
                    String distanceText = String.valueOf(sensor10Distance) + "cm";
                    String scoreText = String.valueOf(score);
                    ((TextView) findViewById(R.id.main_sensor10_speed)).setText(speedText);
                    ((TextView) findViewById(R.id.main_sensor10_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor10_score)).setText(scoreText);
                    //finish
                    lastPassedSensor = 10;
                } else if (isSensorPassed("sensor11")) {
                    //set about time and pass
                    long time = totalTime - sensor8Time;
                    String timeText = String.valueOf(time) + "ms";
                    ((TextView) findViewById(R.id.main_sensor11_time)).setText(timeText);
                    ((TextView) findViewById(R.id.main_sensor11_pass)).setText("O");
                    // set about score
                    long score = 0;
                    if (time < (sensor8SetTime / 2)) {
                        long timeGap = (sensor8SetTime / 2) - time;
                        score -= timeGap / 500;
                    } else if (time > (sensor8SetTime / 2)) {
                        long timeGap = time - (sensor8SetTime / 2);
                        score += timeGap / 500;
                    }
                    ((TextView) findViewById(R.id.main_sensor11_score)).setText(String.valueOf(score));
                    lastPassedSensor = 11;
                } else if (isSensorPassed("sensor12")) {
                    //set for 12-17 button
                    sensor12Time = totalTime;
                    ((TextView) findViewById(R.id.main_sensor12_pass)).setText("O");
                    lastPassedSensor = 12;
                } else if (isSensorPassed("sensor13")) {
                    ((TextView) findViewById(R.id.main_sensor7_pass)).setText("O");
                    ((TextView) findViewById(R.id.main_sensor7_pass)).setText(String.valueOf(score));
                    lastPassedSensor = 7;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void resetAllSensors() {

    }

    private void showResultDialog() {

    }

    private void setSensor1() {
        findViewById(R.id.main_sensor1_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서1-18 설정")
                        .setMessage("만점 기준이 될 시간을 ms 단위로 입력하세요.")
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
                                    sensor1SetTime = Integer.parseInt(input.getText().toString());
                                    setSensorNumber += 1;
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
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
                                    resetAllSensors();
                                    mDatabase.child("sensor1").child("start").setValue(1);
                                    Toast.makeText(MainActivity.this, "지금부터 측정이 시작됩니다!", Toast.LENGTH_LONG).show();
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
                        .setTitle("센서2-7 설정")
                        .setMessage("만점 기준이 될 시간을 ms 단위로 입력하세요.")
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
                                    sensor2SetTime = Integer.parseInt(input.getText().toString());
                                    setSensorNumber += 1;
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
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
                        .setTitle("센서3-4 설정")
                        .setMessage("만점 기준이 될 속도를 km/h 단위로 입력하세요.")
                        .setView(input)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    sensor3SetSpeed = Integer.parseInt(input.getText().toString());
                                    final EditText input2 = new EditText(MainActivity.this);
                                    input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("센서3-4 설정")
                                            .setMessage("해당 구간의 간격을 cm 단위로 입력하세요.")
                                            .setView(input2)
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        sensor3SetGap = Integer.parseInt(input2.getText().toString());
                                                        setSensorNumber += 1;
                                                        Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }).show();
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
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

    private void setSensor5() {
        findViewById(R.id.main_sensor5_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서5-6 설정")
                        .setMessage("만점 기준이 될 속도를 km/h 단위로 입력하세요.")
                        .setView(input)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    sensor5SetSpeed = Integer.parseInt(input.getText().toString());
                                    final EditText input2 = new EditText(MainActivity.this);
                                    input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("센서5-6 설정")
                                            .setMessage("해당 구간의 간격을 cm 단위로 입력하세요.")
                                            .setView(input2)
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        sensor5SetGap = Integer.parseInt(input2.getText().toString());
                                                        setSensorNumber += 1;
                                                        Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }).show();
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
    }

    private void setSensor6() {
        findViewById(R.id.main_sensor6_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor6_score)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor6_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor6_score)).setText(scoreAdmit);
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

    private void setSensor7() {
        findViewById(R.id.main_sensor7_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor7_admit)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor7_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor7_score)).setText(scoreAdmit);
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

    private void setSensor8() {
        findViewById(R.id.main_sensor8_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서8-11 설정")
                        .setMessage("만점 기준이 될 시간을 ms 단위로 입력하세요.")
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
                                    sensor8SetTime = Integer.parseInt(input.getText().toString());
                                    setSensorNumber += 1;
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
    }

    private void setSensor9() {
        findViewById(R.id.main_sensor9_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서9-10 설정")
                        .setMessage("만점 기준이 될 속도를 km/h 단위로 입력하세요.")
                        .setView(input)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    sensor9SetSpeed = Integer.parseInt(input.getText().toString());
                                    final EditText input2 = new EditText(MainActivity.this);
                                    input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("센서9-10 설정")
                                            .setMessage("해당 구간의 간격을 cm 단위로 입력하세요.")
                                            .setView(input2)
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        sensor9SetGap = Integer.parseInt(input2.getText().toString());
                                                        setSensorNumber += 1;
                                                        Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }).show();
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
    }

    private void setSensor10() {
        findViewById(R.id.main_sensor10_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor10_score)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor10_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor10_score)).setText(scoreAdmit);
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

    private void setSensor11() {
        findViewById(R.id.main_sensor11_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor11_admit)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor11_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor11_score)).setText(scoreAdmit);
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

    private void setSensor12() {
        findViewById(R.id.main_sensor12_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final RadioGroup select = new RadioGroup(MainActivity.this);
                RadioButton r1 = new RadioButton(MainActivity.this);
                RadioButton r2 = new RadioButton(MainActivity.this);
                RadioButton r3 = new RadioButton(MainActivity.this);
                r1.setText("10~30 이내 통과");
                r2.setText("30~50 이내 통과");
                r3.setText("50~60 이내 통과");
                select.addView(r1);
                select.addView(r2);
                select.addView(r3);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서12-13")
                        .setMessage("통과 기준이 될 폭을 아래 옵션 중 고르세요.")
                        .setView(select)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sensor12Range = select.getCheckedRadioButtonId();
                                Log.d("setSensor12", "sensor12Range: " + String.valueOf(sensor12Range));
                                if (sensor12Range != -1)
                                    setSensorNumber += 1;
                                else
                                    Toast.makeText(MainActivity.this, "옵션이 선택되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor12_setting2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서12-17 설정")
                        .setMessage("만점 기준이 될 시간을 ms 단위로 입력하세요.")
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
                                    sensor12SetTime = Integer.parseInt(input.getText().toString());
                                    setSensorNumber += 1;
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
    }

    private void setSensor13() {
        findViewById(R.id.main_sensor13_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor13_admit)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor13_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor13_score)).setText(scoreAdmit);
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

    private void setSensor14() {
        findViewById(R.id.main_sensor14_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서14-15 설정")
                        .setMessage("만점 기준이 될 속도를 km/h 단위로 입력하세요.")
                        .setView(input)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    sensor14SetSpeed = Integer.parseInt(input.getText().toString());
                                    final EditText input2 = new EditText(MainActivity.this);
                                    input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("센서14-15 설정")
                                            .setMessage("해당 구간의 간격을 cm 단위로 입력하세요.")
                                            .setView(input2)
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        sensor14SetGap = Integer.parseInt(input2.getText().toString());
                                                        setSensorNumber += 1;
                                                        Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }).show();
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor14_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor14_admit)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor14_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor14_score)).setText(scoreAdmit);
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

    private void setSensor15() {
        findViewById(R.id.main_sensor15_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("센서15-16 설정")
                        .setMessage("만점 기준이 될 속도를 km/h 단위로 입력하세요.")
                        .setView(input)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    sensor15SetSpeed = Integer.parseInt(input.getText().toString());
                                    final EditText input2 = new EditText(MainActivity.this);
                                    input2.setInputType(InputType.TYPE_CLASS_NUMBER);
                                    new AlertDialog.Builder(MainActivity.this)
                                            .setTitle("센서15-16 설정")
                                            .setMessage("해당 구간의 간격을 cm 단위로 입력하세요.")
                                            .setView(input2)
                                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    try {
                                                        sensor15SetGap = Integer.parseInt(input2.getText().toString());
                                                        setSensorNumber += 1;
                                                        Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                                    } catch (Exception e) {
                                                        Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            }).show();
                                    Toast.makeText(MainActivity.this, "설정되었습니다.", Toast.LENGTH_LONG).show();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "입력이 올바르지 않습니다.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor15_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor15_admit)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor15_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor15_score)).setText(scoreAdmit);
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


    private void setSensor16() {
        findViewById(R.id.main_sensor16_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor16_score)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor16_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor16_score)).setText(scoreAdmit);
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

    private void setSensor17() {
        findViewById(R.id.main_sensor17_admit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double score = Double.parseDouble(((TextView) findViewById(R.id.main_sensor17_admit)).getText().toString());
                    double admit = Double.parseDouble(((EditText) findViewById(R.id.main_sensor17_input)).getText().toString());
                    if (admit >= 0.5 && admit <= 1.2) {
                        score = score * admit;
                        String scoreAdmit = String.valueOf(score) + "km/h";
                        ((TextView) findViewById(R.id.main_sensor17_score)).setText(scoreAdmit);
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

    private void setSensor18() {
        findViewById(R.id.main_sensor18_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                racing = false;
                mDatabase.child("sensor18").child("pass").setValue(1);
                showResultDialog();
            }
        });
    }

    private DataSnapshot getDatabase(String name) {
        return mSnapshot.child(name);
    }

    private boolean isSensorPassed(String name) {
        int i = Integer.parseInt(name.replace("sensor", ""));
        return ((int) getDatabase(name).child("pass").getValue() != 0) && (i == (lastPassedSensor + 1));
    }

    private boolean isAllSet() {
        return setSensorNumber == 10;
    }
}