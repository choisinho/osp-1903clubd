package app.bqlab.clubd;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
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
import java.util.Timer;

public class MainActivity extends AppCompatActivity {
    //variables
    long sensor2Time = 0;
    long sensor3Time = 0;
    long sensor5Time = 0;
    long sensor9Time = 0;
    long sensor10Time = 0;
    long sensor12Time = 0;
    long sensor13Time = 0;
    long sensor14Time = 0;
    long sensor15Time = 0;
    long sensor3Distance = 0;
    long sensor4Distance = 0;
    long sensor5Distance = 0;
    long sensor6Distance = 0;
    long sensor9Distance = 0;
    long sensor10Distance = 0;
    long sensor12Distance = 0;
    long sensor13Distance = 0;
    long sensor14Distance = 0;
    long sensor15Distance = 0;
    long sensor16Distance = 0;
    long sector1Score = 0;
    long sector2Score = 0;
    long sector3Score = 0;
    long sector4Score = 0;
    long copiedSector1Score = 0;
    long copiedSector2Score = 0;
    long copiedSector3Score = 0;
    long copiedSector4Score = 0;
    boolean devMode;
    boolean[] setSensors = new boolean[10];
    boolean[] passedSensors = new boolean[18];
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
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPreference = getSharedPreferences("setting", MODE_PRIVATE);
        //call methods
        checkDeveloper();
        setSensor1();
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

    private boolean getPreferenceToBoolean(String key) {
        return Boolean.parseBoolean(Objects.requireNonNull(mPreference.getString(key, "false")));
    }

    private boolean isAllSet() {
        if (devMode || getPreferenceToBoolean("allSet"))
            return true;
        for (int i = 0; i < setSensors.length; i++) {
            Log.d(String.valueOf(i), String.valueOf(setSensors[i]));
            if (!setSensors[i])
                return false;
        }
        setPreference("allSet", "true");
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

    private long getScoreBySpeed(long speed, long setSpeed, long maxScore) {
        long score = maxScore / 2;
        if (speed < (setSpeed / 2)) {
            long speedGap = (setSpeed / 2) - speed;
            score -= speedGap / 2;
        } else if (speed > (setSpeed / 2)) {
            long speedGap = speed - (setSpeed / 2);
            score += speedGap / 2;
        }
        if (score < 1)
            score = 1;
        if (score > maxScore)
            score = maxScore;
        return score;
    }

    private long getScoreByTime(long time, long setTime, long maxScore) {
        long score = maxScore / 2;
        if (time < (setTime / 2)) {
            long timeGap = (time / 2) - time;
            score -= timeGap / 500;
        } else if (time > (setTime / 2)) {
            long timeGap = time - (time / 2);
            score += timeGap / 500;
        }
        if (score < 1)
            score = 1;
        if (score > maxScore)
            score = maxScore;
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
            setPreference("sensor5SetGap", "200");
            setPreference("sensor5SetSpeed", "10");
            setPreference("sensor8SetTime", "60000");
            setPreference("sensor9SetGap", "200");
            setPreference("sensor9SetSpeed", "10");
            setPreference("sensor12SetTime", "10");
            setPreference("sensor13SetGap", "200");
            setPreference("sensor13SetSpeed", "10");
            setPreference("sensor14SetGap", "200");
            setPreference("sensor14SetSpeed", "10");
            setPreference("sensor15SetGap", "200");
            setPreference("sensor15SetSpeed", "10");
        }
    }

    private void checkIgnoredSensor(int currentSensor) {
        for (int i = 0; i < 18; i++) {
            if (!passedSensors[i] && i < currentSensor) {
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                        ((TextView) findViewById(R.id.main_sensor3_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor7_score)).setText(String.valueOf(1));
                        break;
                    case 4:
                        ((TextView) findViewById(R.id.main_sensor4_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor5_score)).setText(String.valueOf(1));
                        break;
                    case 5:
                        ((TextView) findViewById(R.id.main_sensor4_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor5_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor6_score)).setText(String.valueOf(1));
                        break;
                    case 6:
                        ((TextView) findViewById(R.id.main_sensor5_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor6_score)).setText(String.valueOf(1));
                        break;
                    case 7:
                        ((TextView) findViewById(R.id.main_sensor3_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor7_score)).setText(String.valueOf(1));
                    case 8:
                        ((TextView) findViewById(R.id.main_sensor11_score)).setText(String.valueOf(1));
                    case 9:
                        ((TextView) findViewById(R.id.main_sensor9_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor10_score)).setText(String.valueOf(1));
                    case 10:
                        ((TextView) findViewById(R.id.main_sensor9_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor10_score)).setText(String.valueOf(1));
                    case 11:
                        ((TextView) findViewById(R.id.main_sensor11_score)).setText(String.valueOf(1));
                    case 12:
                        ((TextView) findViewById(R.id.main_sensor12_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor13_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor17_score)).setText(String.valueOf(1));
                    case 13:
                        ((TextView) findViewById(R.id.main_sensor12_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor13_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor14_score)).setText(String.valueOf(1));
                    case 14:
                        ((TextView) findViewById(R.id.main_sensor13_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor14_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor15_score)).setText(String.valueOf(1));
                    case 15:
                        ((TextView) findViewById(R.id.main_sensor14_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor15_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor16_score)).setText(String.valueOf(1));
                    case 16:
                        ((TextView) findViewById(R.id.main_sensor15_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor16_score)).setText(String.valueOf(1));
                    case 17:
                        ((TextView) findViewById(R.id.main_sensor12_score)).setText(String.valueOf(1));
                        ((TextView) findViewById(R.id.main_sensor17_score)).setText(String.valueOf(1));
                }
            }
        }
    }

    private void setupDatabase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mSnapshot = dataSnapshot;
                if (isPassed("sensor1") && !passedSensors[0]) {
                    passedSensors[0] = true;
                } else if (isPassed("sensor3") && !passedSensors[2]) {
                    checkIgnoredSensor(3);
                    //sensor 2 broken
                    sensor2Time = TimerService.time;
                    ((TextView) findViewById(R.id.main_sensor2_pass)).setText("O");
                    passedSensors[1] = true;
                    //sensor 3 processing
                    sensor3Time = TimerService.time;
                    sensor3Distance = (long) dataSnapshot.child("sensor3").child("distance").getValue();
                    String text = String.valueOf(sensor3Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor3_distance)).setText(text);
                    ((TextView) findViewById(R.id.main_sensor3_score)).setText(String.valueOf(getScoreByDistance(sensor3Distance)));
                    passedSensors[2] = true;
                } else if (isPassed("sensor4") && !passedSensors[3]) {
                    checkIgnoredSensor(4);
                    double time = TimerService.time - sensor3Time;
                    double gap = getPreferenceToDouble("sensor3SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = getPreferenceToInteger("sensor3SetSpeed");
                    String text = String.valueOf((int) speed) + "km/h";
                    sensor4Distance = (long) dataSnapshot.child("sensor4").child("distance").getValue();
                    String distanceText = String.valueOf(sensor4Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor4_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor4_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 7) + getScoreByDistance(sensor4Distance)));
                    passedSensors[3] = true;
                } else if (isPassed("sensor5") && !passedSensors[4]) {
                    checkIgnoredSensor(5);
                    sensor5Time = TimerService.time;
                    sensor5Distance = (long) dataSnapshot.child("sensor5").child("distance").getValue();
                    String text = sensor5Distance + "cm";
                    ((TextView) findViewById(R.id.main_sensor5_distance)).setText(text);
                    ((TextView) findViewById(R.id.main_sensor5_score)).setText(String.valueOf(getScoreByDistance(sensor5Distance)));
                    passedSensors[4] = true;
                } else if (isPassed("sensor6") && !passedSensors[5]) {
                    checkIgnoredSensor(6);
                    double time = TimerService.time - sensor5Time;
                    double gap = getPreferenceToDouble("sensor5SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = getPreferenceToInteger("sensor5SetSpeed");
                    String text = String.valueOf((int) speed) + "km/h";
                    sensor6Distance = (long) dataSnapshot.child("sensor6").child("distance").getValue();
                    String distanceText = String.valueOf(sensor6Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor6_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor6_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 7) + getScoreByDistance(sensor6Distance)));
                    passedSensors[5] = true;
                } else if (isPassed("sensor7") && !passedSensors[6]) {
                    checkIgnoredSensor(7);
                    double time = TimerService.time - sensor2Time;
                    String text = String.valueOf((int) time) + "ms";
                    long score = getScoreByTime(TimerService.time, getPreferenceToInteger("sensor2SetTime"), 2);
                    ((TextView) findViewById(R.id.main_sensor7_pass)).setText("O");
                    ((TextView) findViewById(R.id.main_sensor7_score)).setText(String.valueOf(score));
                    passedSensors[6] = true;
                } else if (isPassed("sensor8") && !passedSensors[7]) {
                    checkIgnoredSensor(8);
                    ((TextView) findViewById(R.id.main_sensor8_pass)).setText("O");
                    passedSensors[7] = true;
                } else if (isPassed("sensor9") && !passedSensors[8]) {
                    checkIgnoredSensor(9);
                    sensor9Time = TimerService.time;
                    sensor9Distance = (long) dataSnapshot.child("sensor9").child("distance").getValue();
                    String text = String.valueOf(sensor9Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor9_distance)).setText(String.valueOf(text));
                    ((TextView) findViewById(R.id.main_sensor9_score)).setText(String.valueOf(getScoreByDistance(sensor9Distance)));
                    passedSensors[8] = true;
                } else if (isPassed("sensor10") && !passedSensors[9]) {
                    checkIgnoredSensor(10);
                    double time = TimerService.time - sensor9Time;
                    double gap = getPreferenceToDouble("sensor9SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = getPreferenceToInteger("sensor9SetSpeed");
                    String text = String.valueOf((int) speed) + "km/h";
                    sensor10Distance = (long) dataSnapshot.child("sensor9").child("distance").getValue();
                    String distanceText = String.valueOf(sensor10Distance);
                    ((TextView) findViewById(R.id.main_sensor10_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor10_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 4) + getScoreByDistance(sensor10Distance)));
                    passedSensors[9] = true;
                } else if (isPassed("sensor11") && !passedSensors[10]) {
                    checkIgnoredSensor(11);
                    double time = TimerService.time - sensor10Time;
                    String text = String.valueOf((int) time) + "ms";
                    long score = getScoreByTime(TimerService.time, getPreferenceToInteger("sensor8SetTime"), 2);
                    ((TextView) findViewById(R.id.main_sensor11_pass)).setText("O");
                    ((TextView) findViewById(R.id.main_sensor11_score)).setText(String.valueOf(score));
                    passedSensors[10] = true;
                } else if (isPassed("sensor12") && !passedSensors[11]) {
                    checkIgnoredSensor(12);
                    sensor12Time = TimerService.time;
                    sensor12Distance = (long) dataSnapshot.child("sensor12").child("distance").getValue();
                    String text = String.valueOf(sensor12Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor12_pass)).setText("O");
                    ((TextView) findViewById(R.id.main_sensor12_score)).setText(String.valueOf(getScoreByDistance(sensor12Distance)));
                    passedSensors[11] = true;
                } else if (isPassed("sensor13") && !passedSensors[12]) {
                    checkIgnoredSensor(13);
                    sensor13Time = TimerService.time;
                    sensor13Distance = (long) dataSnapshot.child("sensor13").child("distance").getValue();
                    String text = String.valueOf(sensor13Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor13_pass)).setText("O");
                    ((TextView) findViewById(R.id.main_sensor13_score)).setText(String.valueOf(getScoreByDistance(sensor12Distance)));
                    passedSensors[12] = true;
                } else if (isPassed("sensor14") && !passedSensors[13]) {
                    checkIgnoredSensor(14);
                    double time = TimerService.time - sensor13Time;
                    double gap = getPreferenceToDouble("sensor13SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = getPreferenceToInteger("sensor13SetSpeed");
                    String text = String.valueOf((int) speed) + "km/h";
                    sensor14Distance = (long) dataSnapshot.child("sensor14").child("distance").getValue();
                    String distanceText = String.valueOf(sensor14Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor14_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor14_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 6) + getScoreByDistance(sensor14Distance)));
                    passedSensors[13] = true;
                } else if (isPassed("sensor15") && !passedSensors[14]) {
                    checkIgnoredSensor(15);
                    double time = TimerService.time - sensor14Time;
                    double gap = getPreferenceToDouble("sensor14SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = getPreferenceToInteger("sensor14SetSpeed");
                    String text = String.valueOf((int) speed) + "km/h";
                    sensor15Distance = (long) dataSnapshot.child("sensor15").child("distance").getValue();
                    String distanceText = String.valueOf(sensor15Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor15_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor15_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 6) + getScoreByDistance(sensor15Distance)));
                    passedSensors[14] = true;
                } else if (isPassed("sensor16") && !passedSensors[15]) {
                    checkIgnoredSensor(16);
                    double time = TimerService.time - sensor15Time;
                    double gap = getPreferenceToDouble("sensor15SetGap");
                    double speed = getSpeedInKmPerH(time, gap);
                    int setSpeed = getPreferenceToInteger("sensor15SetSpeed");
                    String text = String.valueOf((int) speed) + "km/h";
                    sensor16Distance = (long) dataSnapshot.child("sensor16").child("distance").getValue();
                    String distanceText = String.valueOf(sensor16Distance) + "cm";
                    ((TextView) findViewById(R.id.main_sensor16_distance)).setText(distanceText);
                    ((TextView) findViewById(R.id.main_sensor16_score)).setText(String.valueOf(getScoreBySpeed((int) speed, setSpeed, 6) + getScoreByDistance(sensor16Distance)));
                    passedSensors[15] = true;
                } else if (isPassed("sensor17") && !passedSensors[16]) {
                    checkIgnoredSensor(17);
                    long time = TimerService.time - sensor12Time;
                    String text = String.valueOf(time) + "ms";
                    long score = getScoreByTime(TimerService.time, getPreferenceToInteger("sensor12SetTime"), 2);
                    ((TextView) findViewById(R.id.main_sensor17_pass)).setText("O");
                    ((TextView) findViewById(R.id.main_sensor17_score)).setText(String.valueOf(score));
                    passedSensors[16] = true;
                } else if (isPassed("sensor18") && !passedSensors[17]) {
                    checkIgnoredSensor(18);
                    String text = String.valueOf(TimerService.time) + "ms";
                    long score = getScoreByTime(TimerService.time, getPreferenceToInteger("sensor1SetTime"), 10);
                    ((TextView) findViewById(R.id.main_sensor18_time)).setText(text);
                    ((TextView) findViewById(R.id.main_sensor18_score)).setText(String.valueOf(score));
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
                            for (boolean b : passedSensors)
                                b = false;
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
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor3_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor4_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor5_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor6_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor7_score)).getText().toString());
        sector1Score = totalScore;
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor9_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor10_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor11_score)).getText().toString());
        sector2Score = totalScore - sector1Score;
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor12_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor13_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor14_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor15_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor16_score)).getText().toString());
        totalScore += Integer.parseInt(((TextView) findViewById(R.id.main_sensor17_score)).getText().toString());
        sector3Score = totalScore - (sector1Score + sector2Score);
        sector4Score = Integer.parseInt(((TextView) findViewById(R.id.main_sensor18_score)).getText().toString());
        final EditText sector1Input = new EditText(MainActivity.this);
        sector1Input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        sector1Input.setSingleLine();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("경기 종료")
                .setMessage("1구간의 심사위원 점수를 입력하세요.")
                .setView(sector1Input)
                .setCancelable(false)
                .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            double sector1InputScore = Double.parseDouble(sector1Input.getText().toString());
                            if (sector1InputScore >= 0.5 && sector1InputScore <= 1.2) {
                                copiedSector1Score = (long) ((double) sector1Score * sector1InputScore);
                                final EditText sector2Input = new EditText(MainActivity.this);
                                sector2Input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                sector2Input.setSingleLine();
                                new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("경기 종료")
                                        .setMessage("2구간의 심사위원 점수를 입력하세요.")
                                        .setView(sector2Input)
                                        .setCancelable(false)
                                        .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                try {
                                                    double sector2InputScore = Double.parseDouble(sector2Input.getText().toString());
                                                    if (sector2InputScore >= 0.5 && sector2InputScore <= 1.2) {
                                                        copiedSector2Score = (long) ((double) sector2Score * sector2InputScore);
                                                        final EditText sector3Input = new EditText(MainActivity.this);
                                                        sector3Input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                                        sector3Input.setSingleLine();
                                                        new AlertDialog.Builder(MainActivity.this)
                                                                .setTitle("경기 종료")
                                                                .setMessage("3구간의 심사위원 점수를 입력하세요.")
                                                                .setView(sector3Input)
                                                                .setCancelable(false)
                                                                .setPositiveButton("다음", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        try {
                                                                            double sector3InputScore = Double.parseDouble(sector3Input.getText().toString());
                                                                            if (sector3InputScore >= 0.5 && sector3InputScore <= 1.2) {
                                                                                copiedSector3Score = (long) ((double) sector3Score * sector3InputScore);
                                                                                final EditText sector4Input = new EditText(MainActivity.this);
                                                                                sector4Input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                                                                                sector4Input.setSingleLine();
                                                                                new AlertDialog.Builder(MainActivity.this)
                                                                                        .setTitle("경기 종료")
                                                                                        .setMessage("4구간의 심사위원 점수를 입력하세요.")
                                                                                        .setView(sector4Input)
                                                                                        .setCancelable(false)
                                                                                        .setPositiveButton("점수확인", new DialogInterface.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                try {
                                                                                                    double sector4InputScore = Double.parseDouble(sector4Input.getText().toString());
                                                                                                    if (sector4InputScore >= 0.5 && sector4InputScore <= 1.2) {
                                                                                                        copiedSector4Score = (long) ((double) sector4Score * sector4InputScore);
                                                                                                        long score = copiedSector1Score + copiedSector2Score + copiedSector3Score + copiedSector4Score;
                                                                                                        TextView textView = new TextView(MainActivity.this);
                                                                                                        textView.setText("1구간: " + copiedSector1Score + "점\n" + "2구간: " + copiedSector2Score + "점\n" + "3구간: " + copiedSector3Score + "점\n" + "4구간: " + copiedSector4Score + "점\n" + "총점: " + String.valueOf(score) + "점\n");
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
                                                                            } else
                                                                                Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                                                        } catch (NumberFormatException e) {
                                                                            Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                                                        }
                                                                    }
                                                                }).show();
                                                    } else
                                                        Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                                } catch (NumberFormatException e) {
                                                    Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }).show();
                            } else
                                Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도해주세요.", Toast.LENGTH_LONG).show();
                        } catch (NumberFormatException e) {
                            Toast.makeText(MainActivity.this, "잘못된 입력입니다. 다시 시도하세요.", Toast.LENGTH_LONG).show();
                        }
                    }
                }).show();
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
                    mDatabase.child("sensor8").child("distance").setValue(0);
                    mDatabase.child("sensor9").child("distance").setValue(0);
                    mDatabase.child("sensor9").child("pass").setValue(0);
                    mDatabase.child("sensor10").child("distance").setValue(0);
                    mDatabase.child("sensor10").child("pass").setValue(0);
                    mDatabase.child("sensor11").child("distance").setValue(0);
                    mDatabase.child("sensor11").child("pass").setValue(0);
                    mDatabase.child("sensor12").child("pass").setValue(0);
                    mDatabase.child("sensor12").child("distance").setValue(0);
                    mDatabase.child("sensor13").child("pass").setValue(0);
                    mDatabase.child("sensor13").child("distance").setValue(0);
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

    private void setSensor18() {
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