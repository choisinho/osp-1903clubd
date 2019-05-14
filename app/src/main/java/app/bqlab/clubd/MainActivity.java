package app.bqlab.clubd;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //Constants
    String TAG = "MainActivity";
    //Variables
    boolean[] passedSensors;
    //objects
    SharedPreferences mPreference;
    DatabaseReference mDatabase;
    DataSnapshot mSnapshot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        setupDatabase();
        initializeData();
    }

    private void init() {
        //initialize
        passedSensors = new boolean[18];
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPreference = getSharedPreferences("setting", MODE_PRIVATE);
        //call methods
        setTime();
        setEndButton();
    }

    private void setPreference(String key, String value) {
        mPreference.edit().putString(key, value).apply();
    }

    private int getTime() {
        return Integer.parseInt(Objects.requireNonNull(mPreference.getString("time", "0")));
    }

    private void setupDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSnapshot = dataSnapshot;
                if (isPassed("sensor1")) {
                    passedSensors[0] = true;
                } else if (isPassed("sensor2")) {
                    long distance = getDistance("sensor2");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor2_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor2_pass)).setText("O");
                    if (getDistance("sensor2") <= 70)
                        ((TextView) findViewById(R.id.main_sensor2_score)).setText("2");
                    passedSensors[1] = true;
                } else if (isPassed("sensor3")) {
                    long distance = getDistance("sensor3");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor3_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor3_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor3_score)).setText("0");
                    passedSensors[2] = true;
                } else if (isPassed("sensor4")) {
                    long distance = getDistance("sensor4");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor4_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor4_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor4_score)).setText("0");
                    passedSensors[3] = true;
                } else if (isPassed("sensor5")) {
                    long distance = getDistance("sensor5");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor5_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor5_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor5_score)).setText("0");
                    passedSensors[4] = true;
                } else if (isPassed("sensor6")) {
                    long distance = getDistance("sensor6");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor6_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor6_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor6_score)).setText("0");
                    passedSensors[5] = true;
                } else if (isPassed("sensor7")) {
                    long distance = getDistance("sensor7");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor7_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor7_pass)).setText("O");
                    if (getDistance("sensor7") <= 70)
                        ((TextView) findViewById(R.id.main_sensor7_score)).setText("2");
                    passedSensors[6] = true;
                } else if (isPassed("sensor8")) {
                    long distance = getDistance("sensor8");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor8_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor8_pass)).setText("O");
                    if (getDistance("sensor8") <= 70)
                        ((TextView) findViewById(R.id.main_sensor8_score)).setText("2");
                    passedSensors[7] = true;
                } else if (isPassed("sensor9")) {
                    long distance = getDistance("sensor9");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor9_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor9_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor9_score)).setText("0");
                    passedSensors[8] = true;
                } else if (isPassed("sensor10")) {
                    long distance = getDistance("sensor10");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor10_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor10_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor10_score)).setText("0");
                    passedSensors[9] = true;
                } else if (isPassed("sensor11")) {
                    long distance = getDistance("sensor11");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor11_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor11_pass)).setText("O");
                    if (getDistance("sensor11") <= 70)
                        ((TextView) findViewById(R.id.main_sensor11_score)).setText("2");
                    passedSensors[10] = true;
                } else if (isPassed("sensor12")) {
                    long distance = getDistance("sensor12");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor12_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor12_pass)).setText("O");
                    if (getDistance("sensor12") <= 70)
                        ((TextView) findViewById(R.id.main_sensor12_score)).setText("2");
                    passedSensors[11] = true;
                } else if (isPassed("sensor13")) {
                    long distance = getDistance("sensor13");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor13_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor13_pass)).setText("O");
                    if (getDistance("sensor13") <= 70)
                        ((TextView) findViewById(R.id.main_sensor13_score)).setText("2");
                    passedSensors[12] = true;
                } else if (isPassed("sensor14")) {
                    long distance = getDistance("sensor14");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor14_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor14_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor14_score)).setText("0");
                    passedSensors[13] = true;
                } else if (isPassed("sensor15")) {
                    long distance = getDistance("sensor15");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor15_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor15_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor15_score)).setText("0");
                    passedSensors[14] = true;
                } else if (isPassed("sensor16")) {
                    long distance = getDistance("sensor16");
                    long score = 3 + ((50 - distance) / 10);
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor16_distance)).setText(distanceText);
                    if (distance <= 50)
                        ((TextView) findViewById(R.id.main_sensor16_score)).setText(String.valueOf(score));
                    else
                        ((TextView) findViewById(R.id.main_sensor16_score)).setText("0");
                    passedSensors[15] = true;
                } else if (isPassed("sensor17")) {
                    long distance = getDistance("sensor17");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor17_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor17_pass)).setText("O");
                    if (getDistance("sensor17") <= 70)
                        ((TextView) findViewById(R.id.main_sensor17_score)).setText("2");
                    passedSensors[16] = true;
                } else if (isPassed("sensor18")) {
                    long time = TimerService.time;
                    long score = (getTime() + (getTime() - time)) / 1000;
                    if (score <= 0)
                        score = 0;
                    String timeText = time + "ms";
                    ((TextView) findViewById(R.id.main_sensor18_time)).setText(timeText);
                    ((TextView) findViewById(R.id.main_sensor18_score)).setText(String.valueOf(score));
                    long distance = getDistance("sensor18");
                    String distanceText = distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor18_distance)).setText(distanceText);
                    passedSensors[17] = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void initializeData() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((long) dataSnapshot.child("sensor1").child("start").getValue() != 0) {
                    mPreference.edit().putString("time", "0").apply();
                    new AsyncTask<Void, Void, Void>() {
                        AlertDialog dialog;

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            dialog.dismiss();
                            refresh();
                        }

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            if (ServiceCheck.isRunning(MainActivity.this, TimerService.class.getName()))
                                stopService(new Intent(MainActivity.this, TimerService.class));
                            ProgressBar progressBar = new ProgressBar(MainActivity.this);
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("데이터를 초기화하는 중입니다..");
                            builder.setView(progressBar);
                            builder.setCancelable(false);
                            dialog = builder.show();
                            TimerService.racing = false;
                            resetDatabase();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }.execute();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showResultDialog() {
        long totalScore = 0;
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor2_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor3_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor4_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor5_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor6_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor7_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor9_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor10_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor11_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor12_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor13_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor14_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor15_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor16_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor17_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor18_score)).getText().toString());
        final EditText input = new EditText(MainActivity.this);
        input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setSingleLine();
        final long finalTotalScore = totalScore;
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("경기 종료")
                .setMessage("심사위원 점수를 입력하세요.(0.5~1.5)")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("점수확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            double inputScore = Double.parseDouble(input.getText().toString());
                            if (inputScore >= 0.5 && inputScore <= 1.5) {
                                double totalScore = finalTotalScore;
                                totalScore *= inputScore;
                                if (totalScore <= 0) {
                                    totalScore = 0;
                                }
                                String scoreText = "총점: " + totalScore + "점\n";
                                TextView textView = new TextView(MainActivity.this);
                                textView.setText(scoreText);
                                textView.setGravity(Gravity.CENTER);
                                textView.setTextSize(30);
                                textView.setTextColor(Color.BLACK);
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("경기 종료")
                                        .setMessage("이 플레이어의 경기 결과입니다.")
                                        .setView(textView)
                                        .setPositiveButton("다시하기", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                initializeData();
                                            }
                                        });
                                AlertDialog alertDialog = builder.create();
                                Objects.requireNonNull(alertDialog.getWindow()).setLayout(600, 800);
                                alertDialog.show();
                            } else
                                Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
    }

    private boolean isPassed(String sensor) {
        int senserNumber = Integer.parseInt(sensor.replace("sensor", "")) - 1;
        return (((long) mSnapshot.child(sensor).child("pass").getValue()) == 1) && !passedSensors[senserNumber];
    }

    private long getDistance(String sensor) {
        return (long) mSnapshot.child(sensor).child("distance").getValue();
    }

    private void refresh() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    private void resetDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mDatabase.child("sensor1").child("start").setValue(0);
                    Thread.sleep(3000);
                    mDatabase.child("sensor1").child("pass").setValue(0);
                    mDatabase.child("sensor2").child("distance").setValue(0);
                    mDatabase.child("sensor2").child("pass").setValue(0);
                    mDatabase.child("sensor3").child("distance").setValue(0);
                    mDatabase.child("sensor3").child("pass").setValue(0);
                    mDatabase.child("sensor4").child("distance").setValue(0);
                    mDatabase.child("sensor4").child("pass").setValue(0);
                    mDatabase.child("sensor5").child("distance").setValue(0);
                    mDatabase.child("sensor5").child("pass").setValue(0);
                    mDatabase.child("sensor6").child("distance").setValue(0);
                    mDatabase.child("sensor6").child("pass").setValue(0);
                    mDatabase.child("sensor7").child("distance").setValue(0);
                    mDatabase.child("sensor7").child("pass").setValue(0);
                    mDatabase.child("sensor8").child("distance").setValue(0);
                    mDatabase.child("sensor8").child("pass").setValue(0);
                    mDatabase.child("sensor9").child("distance").setValue(0);
                    mDatabase.child("sensor9").child("pass").setValue(0);
                    mDatabase.child("sensor10").child("distance").setValue(0);
                    mDatabase.child("sensor10").child("pass").setValue(0);
                    mDatabase.child("sensor11").child("distance").setValue(0);
                    mDatabase.child("sensor11").child("pass").setValue(0);
                    mDatabase.child("sensor12").child("distance").setValue(0);
                    mDatabase.child("sensor12").child("pass").setValue(0);
                    mDatabase.child("sensor13").child("distance").setValue(0);
                    mDatabase.child("sensor13").child("pass").setValue(0);
                    mDatabase.child("sensor14").child("distance").setValue(0);
                    mDatabase.child("sensor14").child("pass").setValue(0);
                    mDatabase.child("sensor15").child("distance").setValue(0);
                    mDatabase.child("sensor15").child("pass").setValue(0);
                    mDatabase.child("sensor16").child("distance").setValue(0);
                    mDatabase.child("sensor16").child("pass").setValue(0);
                    mDatabase.child("sensor17").child("distance").setValue(0);
                    mDatabase.child("sensor17").child("pass").setValue(0);
                    mDatabase.child("sensor18").child("pass").setValue(0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setTime() {
        findViewById(R.id.main_sensor1_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText e = new EditText(MainActivity.this);
                e.setInputType(InputType.TYPE_CLASS_NUMBER);
                e.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("기본시간 설정")
                        .setMessage("기준이 될 시간을 ms 단위로 입력하세요.")
                        .setView(e)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!e.getText().toString().isEmpty()) {
                                    Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    setPreference("time", e.getText().toString());
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor1_reset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeData();
            }
        });
        findViewById(R.id.main_sensor1_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTime() > 0) {
                    if (!TimerService.racing) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("경기 시작")
                                .setMessage("아래의 버튼을 누르면 시간 측정이 시작됩니다.")
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("경기시작", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mDatabase.child("sensor1").child("start").setValue(1);
                                        Toast.makeText(MainActivity.this, "잠시후 경기가 시작됩니다.", Toast.LENGTH_LONG).show();
                                        new CountDownTimer(3000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {

                                            }

                                            @Override
                                            public void onFinish() {
                                                startService(new Intent(MainActivity.this, TimerService.class));
                                                TimerService.racing = true;
                                            }
                                        }.start();
                                    }
                                }).show();
                    } else
                        Toast.makeText(MainActivity.this, "이미 경기가 진행중입니다.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(MainActivity.this, "시간을 설정하지 않았습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setEndButton() {
        findViewById(R.id.main_sensor18_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TimerService.racing) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("경기 종료")
                            .setMessage("아래 버튼을 눌러 현재 경기를 종료합니다.")
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showResultDialog();
                                }
                            }).show();
                } else
                    Toast.makeText(MainActivity.this, "아직 경기가 시작되지 않았습니다.", Toast.LENGTH_LONG).show();

            }
        });
    }
}