package app.bqlab.clubd;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
    //variables
    boolean devMode;
    boolean[] setSensors = new boolean[3]; //test
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
        startService(new Intent(this, DeleteService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void init() {
        //initialize
        devMode = true;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPreference = getSharedPreferences("setting", MODE_PRIVATE);
        //call methods
        checkDeveloper();
        setSensor1();
        setSensor2();
        setSensor3();
        setSensor18();
    }

    private void setPreference(String key, String value) {
        mPreference.edit().putString(key, value).apply();
    }

    private String getPreference(String key) {
        return mPreference.getString(key, "");
    }

    private long getPreferenceToLong(String key) {
        return Long.parseLong(Objects.requireNonNull(mPreference.getString(key, "0")));
    }

    private double getPreferenceToDouble(String key) {
        return Double.parseDouble(Objects.requireNonNull(mPreference.getString(key, "0")));
    }

    private boolean getPreferenceToBoolean(String key) {
        return Boolean.parseBoolean(Objects.requireNonNull(mPreference.getString(key, "false")));
    }

    private boolean isAllSet() {
        if (devMode)
            return true;
        for (boolean b : setSensors)
            if (!b)
                return false;
        return true;
    }

    private boolean isPassed(String sensor) {
        return ((long) mSnapshot.child(sensor).child("pass").getValue()) == 1;
    }

    private int getScoreByDistance(int distance) {
        if (distance < 10)
            return 7;
        else if (distance < 15)
            return 6;
        else if (distance < 20)
            return 5;
        else
            return 1;
    }

    private int getScoreBySpeed(int speed, int setSpeed, int maxScore) {
        int score = maxScore / 2;
        if (speed < (setSpeed / 2)) {
            int speedGap = (setSpeed / 2) - speed;
            score -= (long) (speedGap / 2);
        } else if (speed > (setSpeed / 2)) {
            int speedGap = speed - (setSpeed / 2);
            score += (long) (speedGap / 2);
        }
        return score;
    }

    private double getSpeedInKmPerH(double ms, double cm) {
        double km = cm / 100000d;
        Log.d("km", String.valueOf(km));
        double h = ms / 3600000d;
        Log.d("h", String.valueOf(h));
        return km / h;
    }

    private void checkDeveloper() {
        if (devMode) {
            setPreference("sensor1SetTime", "600000");
            setPreference("sensor2SetTime", "60000");
            setPreference("sensor3SetGap", "20");
            setPreference("sensor3SetSpeed", "10");
        }
    }

    private void setupDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSnapshot = dataSnapshot;
                if (isPassed("sensor2")) {
                    ((TextView) findViewById(R.id.main_sensor2_pass)).setText("O");
                }
                if (isPassed("sensor3")) {
                    setPreference("sensor3Time", String.valueOf(getPreferenceToLong("time")));
                    Log.d("3time", String.valueOf(getPreferenceToDouble("sensor3Time")));
                    setPreference("sensor3Distance", String.valueOf(dataSnapshot.child("sensor3").child("distance").getValue()));
                    ((TextView) findViewById(R.id.main_sensor3_distance)).setText(getPreference("sensor3Distance"));
                    ((TextView) findViewById(R.id.main_sensor3_score)).setText(String.valueOf(getScoreByDistance((int) getPreferenceToLong("sensor3Distance"))));
                }
                if (isPassed("sensor4")) {
                    Log.d("time", String.valueOf(getPreferenceToDouble("time")));
                    Log.d("3time", String.valueOf(getPreferenceToDouble("sensor3Time")));
                    double time = getPreferenceToDouble("time") - getPreferenceToDouble("sensor3Time");
                    Log.d("minustime", String.valueOf(time));
                    double gap = getPreferenceToDouble("sensor3SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = (int) getPreferenceToLong("sensor3SetSpeed");
                    Log.d("time", String.valueOf(time));
                    Log.d("gap", String.valueOf(gap));
                    Log.d("speed", String.valueOf(speed));
                    Log.d("setSpeed", String.valueOf(setSpeed));
                    String text = String.valueOf((int) speed) + "km/h";
                    setPreference("sensor4Distance", String.valueOf(dataSnapshot.child("sensor4").child("distance").getValue()));
                    ((TextView) findViewById(R.id.main_sensor4_distance)).setText(getPreference("sensor4Distance"));
                    ((TextView) findViewById(R.id.main_sensor4_speed)).setText(text);
                    ((TextView) findViewById(R.id.main_sensor4_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 7)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void resetDatabase() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mDatabase.child("sensor1").child("start").setValue(0);
                    Thread.sleep(3000);
                    mDatabase.child("sensor1").child("pass").setValue(0);
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
                    mDatabase.child("sensor8").child("pass").setValue(0);
                    mDatabase.child("sensor9").child("distance").setValue(0);
                    mDatabase.child("sensor9").child("pass").setValue(0);
                    mDatabase.child("sensor10").child("distance").setValue(0);
                    mDatabase.child("sensor10").child("pass").setValue(0);
                    mDatabase.child("sensor11").child("distance").setValue(0);
                    mDatabase.child("sensor11").child("pass").setValue(0);
                    mDatabase.child("sensor12").child("pass").setValue(0);
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
                    mPreference.edit().remove("time").apply();
                    mPreference.edit().remove("sensor1SetTime").apply();
                    mPreference.edit().remove("sensor2SetTime").apply();
                    mPreference.edit().remove("sensor3SetGap").apply();
                    mPreference.edit().remove("sensor3SetSpeed").apply();
                    mPreference.edit().remove("sensor3Time").apply();
                    mPreference.edit().remove("sensor3Distance").apply();
                    mPreference.edit().remove("sensor3SetGap").apply();
                    mPreference.edit().remove("sensor3SetSpeed").apply();
                    mPreference.edit().remove("sensor4Distance").apply();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void resetLayouts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //        ((TextView)findViewById(R.id.main_sensor2_pass)).setText("X");
                        //        ((TextView)findViewById(R.id.main_sensor2_distance)).setText("0cm");
                        //        ((TextView)findViewById(R.id.main_sensor2_score)).setText("0");
                        //        ((TextView)findViewById(R.id.main_sensor2_speed)).setText("0km/h");
                        //        ((TextView)findViewById(R.id.main_sensor2_time)).setText("0ms");
                        ((TextView) findViewById(R.id.main_sensor2_pass)).setText("X");
                        ((TextView) findViewById(R.id.main_sensor3_distance)).setText("0cm");
                        ((TextView) findViewById(R.id.main_sensor3_score)).setText("0");
                        ((TextView) findViewById(R.id.main_sensor4_distance)).setText("0cm");
                        ((TextView) findViewById(R.id.main_sensor4_score)).setText("0");
                        ((TextView) findViewById(R.id.main_sensor4_speed)).setText("0km/h");
                    }
                });
            }
        }).start();
    }

    private void setSensor1() {
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
                                Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                setPreference("sensor1SetTime", e.getText().toString());
                                setSensors[0] = true;
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor1_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllSet()) {
                    if (!getPreferenceToBoolean("racing")) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("경기 시작")
                                .setMessage("아래의 버튼을 누르면 측정이 시작됩니다.")
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                })
                                .setPositiveButton("경기시작", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        setPreference("racing", "true");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                while (getPreferenceToBoolean("racing")) {
                                                    try {
                                                        Thread.sleep(1);
                                                        mDatabase.child("sensor1").child("start").setValue(1);
                                                        Log.d("now", getPreference("time"));
                                                        setPreference("time", String.valueOf(getPreferenceToLong("time") + 1));
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }).start();
                                    }
                                }).show();
                    } else
                        Toast.makeText(MainActivity.this, "이미 경기가 진행중입니다.", Toast.LENGTH_LONG).show();
                } else
                    Toast.makeText(MainActivity.this, "아직 설정을 모두 마치지 않았습니다.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setSensor2() {
        findViewById(R.id.main_sensor2_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText e = new EditText(MainActivity.this);
                e.setInputType(InputType.TYPE_CLASS_NUMBER);
                e.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("기본시간 설정")
                        .setMessage("기준이 될 시간을 ms 단위로 입력하세요.")
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                setPreference("sensor2SetTime", e.getText().toString());
                                setSensors[1] = true;
                            }
                        }).show();
            }
        });
    }

    private void setSensor3() {
        findViewById(R.id.main_sensor3_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText gapInput = new EditText(MainActivity.this);
                gapInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                gapInput.setSingleLine();
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("기본간격 설정")
                        .setMessage("해당 구간의 간격을 cm 단위로 입력하세요.")
                        .setView(gapInput)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setPreference("sensor3SetGap", gapInput.getText().toString());
                                final EditText speedInput = new EditText(MainActivity.this);
                                speedInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                                speedInput.setSingleLine();
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("기본속도 설정")
                                        .setMessage("기준이 될 속도를 km/h 단위로 입력하세요.")
                                        .setView(speedInput)
                                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                            }
                                        })
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                setPreference("sensor3SetSpeed", speedInput.getText().toString());
                                                setSensors[2] = true;
                                            }
                                        }).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor18() {
        findViewById(R.id.main_sensor18_end).setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {
                    AlertDialog dialog;
                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        dialog.dismiss();
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        ProgressBar progressBar = new ProgressBar(MainActivity.this);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("데이터를 초기화하는 중입니다..");
                        builder.setView(progressBar);
                        builder.setCancelable(false);
                        dialog = builder.show();
                        setPreference("racing", "false");
                        resetLayouts();
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
        });
    }
}