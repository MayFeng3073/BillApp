package com.example.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.room.Room;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.ExpenseEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseMonitorService extends Service {
    private static final String TAG = "ExpenseMonitorService";
    private boolean isRunning = false;
    private Handler handler = new Handler();
    private Runnable runnable;

    // 数据库（使用同步查询，确保能拿到数据）
    private AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service 创建：后台账目监控已启动");
        db = AppDatabase.getInstance(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;

        runnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    // 必须用同步线程获取数据
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // 获取今天日期
                                String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                                // ✅ 关键：使用 SimpleSQL 查询，不走 LiveData
                                List<ExpenseEntity> list = db.expenseDao().getAllExpensesSync();

                                int count = 0;
                                for (ExpenseEntity e : list) {
                                    if (e.getDate() != null && e.getDate().startsWith(today)) {
                                        count++;
                                    }
                                }

                                Log.d(TAG, "【后台任务】今日已记账：" + count + " 笔");

                            } catch (Exception e) {
                                Log.d(TAG, "【后台任务】查询失败");
                            }
                        }
                    }).start();

                    handler.postDelayed(this, 5000);
                }
            }
        };

        handler.post(runnable);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        handler.removeCallbacks(runnable);
        Log.d(TAG, "Service 销毁：后台账目监控已停止");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}