package app.bqlab.clubd;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;

public class TimerService extends Service {

    public static boolean racing;
    public static long time;
    static final String TAG = "TimerService";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        time = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (racing) {
                        Thread.sleep(1);
                        time += 1;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_NOT_STICKY;
    }
}
