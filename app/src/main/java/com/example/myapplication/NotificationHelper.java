package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    // 通知渠道ID（唯一，不可修改）
    private static final String CHANNEL_ID = "expense_reminder_channel";
    private static final String CHANNEL_NAME = "记账提醒";
    private static final int NOTIFICATION_ID = 1001; // 固定通知ID，避免重复创建

    private final Context context;
    private final NotificationManager notificationManager;

    public NotificationHelper(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    // ✅ 优化通知渠道：确保重要性足够，能显示通知+提醒
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 重要性设为 HIGH：会弹出横幅通知，状态栏显示
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH // 从DEFAULT改为HIGH，确保提醒
            );
            channel.setDescription("记账提醒通知，点击跳转到记账页面");
            channel.setShowBadge(true); // 显示桌面角标
            channel.enableVibration(true); // 允许震动
            channel.enableLights(true); // 允许指示灯
            notificationManager.createNotificationChannel(channel);
        }
    }

    // ✅ 发送记账提醒通知（优化PendingIntent和通知构建）
    public void sendExpenseReminder() {
        // 1. 点击通知跳回MainActivity
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // 2. PendingIntent适配高版本Android（必须用IMMUTABLE）
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 3. 构建通知（优化显示效果）
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // 通知小图标（确保存在）
                .setContentTitle("记账提醒")
                .setContentText("今天还没有记录支出，点击立即记账")
                .setPriority(NotificationCompat.PRIORITY_HIGH) // 高优先级，确保显示
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE) // 声音+震动提醒
                .setAutoCancel(true) // 点击通知后自动消失
                .setContentIntent(pendingIntent) // 绑定跳转
                .setOnlyAlertOnce(true); // 同一通知只提醒一次

        // 4. 发送通知
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}