package com.kost.cou;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static android.telephony.AvailableNetworkInfo.PRIORITY_HIGH;

public class MyReceiver extends BroadcastReceiver {

    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Pressure chanel";

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        createChannelIfNeeded(notificationManager);

        notificationManager.notify(NOTIFY_ID, createNotificationBuilder(context));
    }

    public Notification createNotificationBuilder(Context context) {

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("Надо померить давление")
                        .setPriority(PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setVibrate(new long[] {1000, 1000, 1000, 1000})
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        notificationBuilder.setContentIntent(createPendingIntent(context));
        return notificationBuilder.build();
    }

    public PendingIntent createPendingIntent(Context context) {
        Intent actionIntent = new Intent(context, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                context,
                0,
                actionIntent,
                PendingIntent.FLAG_IMMUTABLE);

        return actionPendingIntent;
    }

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel =
                    new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}