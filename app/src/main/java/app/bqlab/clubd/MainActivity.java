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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    //variables
    long sensor1Time = 0;
    long sensor2Time = 0;
    long sensor3Time = 0;
    long sensor4Time = 0;
    long sensor5Time = 0;
    long sensor6Time = 0;
    long sensor7Time = 0;
    long sensor8Time = 0;
    long sensor9Time = 0;
    long sensor10Time = 0;
    long sensor11Time = 0;
    long sensor12Time = 0;
    long sensor13Time = 0;
    long sensor14Time = 0;
    long sensor15Time = 0;
    long sensor16Time = 0;
    long sensor17Time = 0;
    long sensor1Distance = 0;
    long sensor2Distance = 0;
    long sensor3Distance = 0;
    long sensor4Distance = 0;
    long sensor5Distance = 0;
    long sensor6Distance = 0;
    long sensor7Distance = 0;
    long sensor8Distance = 0;
    long sensor9Distance = 0;
    long sensor10Distance = 0;
    long sensor11Distance = 0;
    long sensor12Distance = 0;
    long sensor13Distance = 0;
    long sensor14Distance = 0;
    long sensor15Distance = 0;
    long sensor16Distance = 0;
    long sensor17Distance = 0;
    boolean devMode, racing;
    boolean[] setSensors = new boolean[11]; //test
    boolean[] passedSensors = new boolean[18]; //test
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
        setSensor5();
        setSensor8();
        setSensor9();
        setSensor12();
        setSensor13();
        setSensor14();
        setSensor15();
        setSensor18();
    }

    private void setPreference(String key, String value) {
        mPreference.edit().putString(key, value).apply();
    }

    private int getPreferenceToInteger(String key) {
        return Integer.parseInt(Objects.requireNonNull(mPreference.getString(key, "0")));
    }

    private double getPreferenceToDouble(String key) {
        return Double.parseDouble(Objects.requireNonNull(mPreference.getString(key, "0")));
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

    private long getScoreByDistance(long distance) {
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
        return (cm * 36) / ms;
    }

    private void checkDeveloper() {
        if (devMode) {
            setPreference("sensor1SetTime", "600000");
            setPreference("sensor2SetTime", "60000");
            setPreference("sensor3SetGap", "200");
            setPreference("sensor3SetSpeed", "10");
        }
    }

    private void setupDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSnapshot = dataSnapshot;
                if (isPassed("sensor1") && !passedSensors[0]) {
                    passedSensors[0] = true;
                } else if (isPassed("sensor2") && !passedSensors[1]) {
                    ((TextView) findViewById(R.id.main_sensor2_pass)).setText("O");
                    passedSensors[1] = true;
                } else if (isPassed("sensor3") && !passedSensors[2]) {
                    sensor3Time = TimerService.time;
                    sensor3Distance = (long) dataSnapshot.child("sensor3").child("distance").getValue();
                    ((TextView) findViewById(R.id.main_sensor3_distance)).setText(String.valueOf(sensor3Distance));
                    ((TextView) findViewById(R.id.main_sensor3_score)).setText(String.valueOf(getScoreByDistance(sensor3Distance)));
                    passedSensors[2] = true;
                } else if (isPassed("sensor4") && !passedSensors[3]) {
                    double time = TimerService.time - sensor3Time;
                    double gap = getPreferenceToDouble("sensor3SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = getPreferenceToInteger("sensor3SetSpeed");
                    String text = String.valueOf((int) speed) + "km/h";
                    sensor4Distance = (long) dataSnapshot.child("sensor4").child("distance").getValue();
                    ((TextView) findViewById(R.id.main_sensor4_distance)).setText(String.valueOf(sensor4Distance));
                    ((TextView) findViewById(R.id.main_sensor4_speed)).setText(text);
                    ((TextView) findViewById(R.id.main_sensor4_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 7)));
                    passedSensors[3] = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void initializeData() {
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
                for (boolean b : passedSensors)
                    b = false;
                ProgressBar progressBar = new ProgressBar(MainActivity.this);
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("데이터를 초기화하는 중입니다..");
                builder.setView(progressBar);
                builder.setCancelable(false);
                dialog = builder.show();
                TimerService.racing = false;
//                resetLayouts();
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
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
                                if (!e.getText().toString().isEmpty()) {
                                    Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    setPreference("sensor1SetTime", e.getText().toString());
                                    setSensors[0] = true;
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor1_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllSet()) {
                    if (!TimerService.racing) {
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
                                        startService(new Intent(MainActivity.this, TimerService.class));
                                        mDatabase.child("sensor1").child("start").setValue(1);
                                        TimerService.racing = true;
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
                                    setPreference("sensor2SetTime", e.getText().toString());
                                    setSensors[1] = true;
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
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
                                if (!gapInput.getText().toString().isEmpty()) {
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
                                                    if (!speedInput.getText().toString().isEmpty()) {
                                                        Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                        setPreference("sensor3SetSpeed", speedInput.getText().toString());
                                                        setSensors[2] = true;
                                                    } else
                                                        Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            }).show();
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor5() {
        findViewById(R.id.main_sensor5_setting).setOnClickListener(new View.OnClickListener() {
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
                                if (!gapInput.getText().toString().isEmpty()) {
                                    setPreference("sensor5SetGap", gapInput.getText().toString());
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
                                                    if (!speedInput.getText().toString().isEmpty()) {
                                                        Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                        setPreference("sensor5SetSpeed", speedInput.getText().toString());
                                                        setSensors[3] = true;
                                                    } else
                                                        Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            }).show();
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor8() {
        findViewById(R.id.main_sensor8_setting).setOnClickListener(new View.OnClickListener() {
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
                                    setPreference("sensor8SetTime", e.getText().toString());
                                    setSensors[4] = true;
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor9() {
        findViewById(R.id.main_sensor9_setting).setOnClickListener(new View.OnClickListener() {
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
                                if (!gapInput.getText().toString().isEmpty()) {
                                    setPreference("sensor9SetGap", gapInput.getText().toString());
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
                                                    if (!speedInput.getText().toString().isEmpty()) {
                                                        Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                        setPreference("sensor9SetSpeed", speedInput.getText().toString());
                                                        setSensors[5] = true;
                                                    }
                                                }
                                            }).show();
                                }
                            }
                        }).show();
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
                        .setTitle("통과기준 설정")
                        .setMessage("통과 기준이 될 항목을 아래에서 고르세요.")
                        .setView(select)
                        .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (select.getCheckedRadioButtonId() != -1) {
                                    Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                    setPreference("sensor12SetRange", String.valueOf(select.getCheckedRadioButtonId()));
                                    setSensors[6] = false;
                                } else
                                    Toast.makeText(MainActivity.this, "선택되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
        findViewById(R.id.main_sensor12_setting2).setOnClickListener(new View.OnClickListener() {
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
                                    setPreference("sensor12SetTime", e.getText().toString());
                                    setSensors[7] = true;
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor13() {
        findViewById(R.id.main_sensor13_setting).setOnClickListener(new View.OnClickListener() {
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
                                if (!gapInput.getText().toString().isEmpty()) {
                                    setPreference("sensor13SetGap", gapInput.getText().toString());
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
                                                    if (!speedInput.getText().toString().isEmpty()) {
                                                        Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                        setPreference("sensor13sSetSpeed", speedInput.getText().toString());
                                                        setSensors[8] = true;
                                                    } else
                                                        Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            }).show();
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor14() {
        findViewById(R.id.main_sensor14_setting).setOnClickListener(new View.OnClickListener() {
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
                                if (!gapInput.getText().toString().isEmpty()) {
                                    setPreference("sensor14SetGap", gapInput.getText().toString());
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
                                                    if (!speedInput.getText().toString().isEmpty()) {
                                                        Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                        setPreference("sensor14SetSpeed", speedInput.getText().toString());
                                                        setSensors[9] = true;
                                                    } else
                                                        Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            }).show();
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor15() {
        findViewById(R.id.main_sensor15_setting).setOnClickListener(new View.OnClickListener() {
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
                                if (!gapInput.getText().toString().isEmpty()) {
                                    setPreference("sensor15SetGap", gapInput.getText().toString());
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
                                                    if (!speedInput.getText().toString().isEmpty()) {
                                                        Toast.makeText(MainActivity.this, "설정이 완료되었습니다.", Toast.LENGTH_LONG).show();
                                                        setPreference("sensor15SetSpeed", speedInput.getText().toString());
                                                        setSensors[10] = true;
                                                    } else
                                                        Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                                                }
                                            }).show();
                                } else
                                    Toast.makeText(MainActivity.this, "입력되지 않았습니다.", Toast.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
    }

    private void setSensor18() {
        findViewById(R.id.main_sensor18_end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                                initializeData();
                            }
                        }).show();
            }
        });
    }
}