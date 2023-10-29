package help.smartbusiness.smartaccounting.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import help.smartbusiness.smartaccounting.R;

public class NotificationHelper {

    public static final String TAG = NotificationHelper.class.getSimpleName();
    private static final String CHANNEL_ID = "help.smartbusiness.smartaccounting.notifications";

    public static void createNotificationChannel(Context context, String channelName, String description) {
        Log.d(TAG, "creating channel");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "channel created");
        }
    }

    public static void simpleNotification(Context context, int header, int message) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(header))
                .setContentText(context.getString(message))
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);
    }

    public static void stickyNotification(Context context, int header, int message, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(header))
                .setContentText(context.getString(message))
                .setOngoing(true)
                .build();
        notificationManager.notify(id, notification);
    }

    public static void actionNotification(Context context, int header, int message, int detail, Intent intent) {
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(header))
                .setContentText(context.getString(message))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(context.getString(detail)))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, notification);
    }

    public static void cancelNotification(Context context, int id) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}
