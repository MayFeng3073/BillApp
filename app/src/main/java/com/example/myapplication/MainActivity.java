package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.adapter.RecordAdapter;
import com.example.myapplication.database.ExpenseEntity;
import com.example.myapplication.viewholder.Expense;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_EXPENSE_UPDATE = "com.example.myapplication.EXPENSE_UPDATE";
    public static final String EXTRA_UPDATE_MSG = "update_message";

    private EditText etAmount, etCategory, etNote;
    private RadioGroup rgType;
    private Button btnAddExpense, btnSendNotification, btnSendBroadcast;
    private TextView tvTotalAmount, tvTotalIncome, tvTotalExpense, tvLatestExpense, tvBroadcastTip;
    private RecyclerView recyclerView;

    // Service 控件
    private Button btnStartService, btnStopService;
    private TextView tvServiceStatus;

    private ExpenseViewModel expenseViewModel;
    private RecordAdapter adapter;
    private NotificationHelper notificationHelper;
    private ExpenseUpdateReceiver expenseUpdateReceiver;

    private Button btnTestProvider; // 独立测试按钮

    private final ActivityResultLauncher<String> requestNotificationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    notificationHelper.sendExpenseReminder();
                    Toast.makeText(this, "通知已发送", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "请开启通知权限", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initViewModel();
        initRecyclerView();
        initListeners();
        registerMyBroadcast();
    }

    private void registerMyBroadcast() {
        expenseUpdateReceiver = new ExpenseUpdateReceiver(tvBroadcastTip);
        IntentFilter filter = new IntentFilter(ACTION_EXPENSE_UPDATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(expenseUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(expenseUpdateReceiver, filter);
        }
    }

    private void sendUpdateBroadcast() {
        Intent intent = new Intent(ACTION_EXPENSE_UPDATE);
        intent.putExtra(EXTRA_UPDATE_MSG, "账目已成功更新！");
        sendBroadcast(intent);
    }

    private void initViews() {
        etAmount = findViewById(R.id.etAmount);
        etCategory = findViewById(R.id.etCategory);
        etNote = findViewById(R.id.etNote);
        rgType = findViewById(R.id.rgType);
        btnAddExpense = findViewById(R.id.btnAddExpense);
        btnSendNotification = findViewById(R.id.btnSendNotification);
        btnSendBroadcast = findViewById(R.id.btn_send_broadcast);
        tvBroadcastTip = findViewById(R.id.tv_broadcast_tip);

        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);
        tvServiceStatus = findViewById(R.id.tv_service_status);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvTotalIncome = findViewById(R.id.tvTotalIncome);
        tvTotalExpense = findViewById(R.id.tvTotalExpense);
        tvLatestExpense = findViewById(R.id.tvLatestExpense);
        recyclerView = findViewById(R.id.recycler_view);

        notificationHelper = new NotificationHelper(this);

        btnTestProvider = findViewById(R.id.btn_test_provider);
    }

    private void initListeners() {

        // 1. 发送广播按钮 → 点击后更新文字
        btnSendBroadcast.setOnClickListener(v -> {
            sendUpdateBroadcast();
            tvBroadcastTip.setText("账目已更新");
            Toast.makeText(this, "手动发送广播成功", Toast.LENGTH_SHORT).show();
        });

        // 2. 添加记录
        btnAddExpense.setOnClickListener(v -> {
            addExpense();
        });

        // 3. 发送通知
        btnSendNotification.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
                    return;
                }
            }
            notificationHelper.sendExpenseReminder();
            Toast.makeText(this, "记账提醒通知已发送！", Toast.LENGTH_SHORT).show();
        });

        // 4. 启动 / 停止后台服务
        if (btnStartService != null && btnStopService != null && tvServiceStatus != null) {
            btnStartService.setOnClickListener(v -> {
                Intent intent = new Intent(this, ExpenseMonitorService.class);
                startService(intent);
                tvServiceStatus.setText("后台状态：运行中（监控账目）");
                Toast.makeText(this, "后台服务已启动", Toast.LENGTH_SHORT).show();
            });

            btnStopService.setOnClickListener(v -> {
                Intent intent = new Intent(this, ExpenseMonitorService.class);
                stopService(intent);
                tvServiceStatus.setText("后台状态：已停止");
                Toast.makeText(this, "后台服务已停止", Toast.LENGTH_SHORT).show();
            });
        }

        // 5. 测试 ContentProvider 独立按钮
        if (btnTestProvider != null) {
            btnTestProvider.setOnClickListener(v -> {
                ContentValues values = new ContentValues();
                values.put("amount", 66.6);
                values.put("category", "Provider测试");
                values.put("note", "接口层插入");
                values.put("date", "2026-04-11");
                values.put("isIncome", false);

                getContentResolver().insert(
                        ExpenseContract.CONTENT_URI,
                        values
                );

                Toast.makeText(this, "已通过 ContentProvider 添加数据", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void addExpense() {
        String amountStr = etAmount.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        boolean isIncome = rgType.getCheckedRadioButtonId() == R.id.rbIncome;

        if (TextUtils.isEmpty(amountStr) || TextUtils.isEmpty(category)) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountStr);
            expenseViewModel.addExpense(amount, category, etNote.getText().toString().trim(), isIncome);
            sendUpdateBroadcast();

            etAmount.setText("");
            etCategory.setText("");
            etNote.setText("");
            Toast.makeText(this, "保存成功！", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "金额输入错误", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViewModel() {
        expenseViewModel = new ViewModelProvider(this).get(ExpenseViewModel.class);
        expenseViewModel.getTotalAmountText().observe(this, s -> tvTotalAmount.setText(s));
        expenseViewModel.getTotalIncomeText().observe(this, s -> tvTotalIncome.setText(s));
        expenseViewModel.getTotalExpenseText().observe(this, s -> tvTotalExpense.setText(s));
        expenseViewModel.getLatestExpenseText().observe(this, s -> tvLatestExpense.setText(s));
        expenseViewModel.getExpenses().observe(this, this::updateList);
    }

    private void updateList(List<ExpenseEntity> entities) {
        List<Expense> list = new ArrayList<>();
        for (ExpenseEntity e : entities) list.add(convert(e));
        adapter.updateData(list);
    }

    private Expense convert(ExpenseEntity e) {
        return new Expense(e.getId(), e.getTypeName(), e.getAmount(), e.getDate(), e.isIncome(), e.getIconResId(), e.getNote());
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecordAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(expenseUpdateReceiver);
    }
}