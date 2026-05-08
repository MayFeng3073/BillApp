package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

public class ExpenseUpdateReceiver extends BroadcastReceiver {
    private final TextView tvTip;

    public ExpenseUpdateReceiver(TextView tvTip) {
        this.tvTip = tvTip;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // 收到广播！
        if (MainActivity.ACTION_EXPENSE_UPDATE.equals(intent.getAction())) {
            String msg = intent.getStringExtra(MainActivity.EXTRA_UPDATE_MSG);

            // 弹出提示
            Toast.makeText(context, "✅ 广播接收成功：" + msg, Toast.LENGTH_LONG).show();
            // 更新页面文字
            tvTip.setText("最新通知：" + msg);
        }
    }
}