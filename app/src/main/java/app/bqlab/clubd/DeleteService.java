package app.bqlab.clubd;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class DeleteService extends Service {

    SharedPreferences mPreference;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mPreference = getSharedPreferences("setting", MODE_PRIVATE);
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
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
    }
}
