package com.example.bailam.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.bailam.R;
import com.example.bailam.database.AppDatabase;

public class ReminderWorker extends Worker {
    private static final String CHANNEL_ID = "REMINDER_CHANNEL";

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        int count = AppDatabase.getInstance(getApplicationContext()).appDao().getIncompleteTaskCount();

        if (count > 0) {
            sendNotification("Nhắc nhở học tập!", "Bạn đang có " + count + " công việc chưa hoàn thành. Hãy mở lịch lên xem nhé!");
        } else {
            // Test mục đích
            sendNotification("Tuyệt vời!", "Bạn đã hoàn thành mọi công việc. Nghỉ ngơi thôi!");
        }

        return Result.success();
    }

    private void sendNotification(String title, String content) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nhắc nhở hàng ngày",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}
