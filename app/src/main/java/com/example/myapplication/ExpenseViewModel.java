package com.example.myapplication;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import com.example.myapplication.database.ExpenseEntity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseViewModel extends AndroidViewModel {
    private final ExpenseRepository repository;
    private final LiveData<List<ExpenseEntity>> allExpenses;
    private final MediatorLiveData<String> totalAmountText;
    private final MediatorLiveData<String> totalIncomeText;
    private final MediatorLiveData<String> totalExpenseText;
    private final MediatorLiveData<String> latestExpenseText;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
        allExpenses = repository.getAllExpenses();

        // 初始化格式化数据
        totalAmountText = new MediatorLiveData<>();
        totalIncomeText = new MediatorLiveData<>();
        totalExpenseText = new MediatorLiveData<>();
        latestExpenseText = new MediatorLiveData<>();

        // 监听数据变化，格式化展示文本
        observeTotalData();
        observeLatestExpense();
    }

    // 监听统计数据
    private void observeTotalData() {
        totalAmountText.addSource(repository.getTotalAmount(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                double total = aDouble == null ? 0 : aDouble;
                totalAmountText.setValue(String.format(Locale.getDefault(), "¥%.2f", total));
            }
        });

        totalIncomeText.addSource(repository.getTotalIncome(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                double total = aDouble == null ? 0 : aDouble;
                totalIncomeText.setValue(String.format(Locale.getDefault(), "收入 ¥%.2f", total));
            }
        });

        totalExpenseText.addSource(repository.getTotalExpense(), new Observer<Double>() {
            @Override
            public void onChanged(Double aDouble) {
                double total = aDouble == null ? 0 : aDouble;
                totalExpenseText.setValue(String.format(Locale.getDefault(), "支出 ¥%.2f", total));
            }
        });
    }

    // 监听最新记录
    private void observeLatestExpense() {
        latestExpenseText.addSource(allExpenses, new Observer<List<ExpenseEntity>>() {
            @Override
            public void onChanged(List<ExpenseEntity> expenseEntities) {
                if (expenseEntities == null || expenseEntities.isEmpty()) {
                    latestExpenseText.setValue("最新记录：暂无记录");
                } else {
                    ExpenseEntity latest = expenseEntities.get(0);
                    latestExpenseText.setValue("最新记录：" + latest.getTypeName() + " - ¥" + latest.getAmount());
                }
            }
        });
    }

    // 添加记录（核心方法）
    public void addExpense(double amount, String category, String note, boolean isIncome) {
        // 获取当前日期
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        // 默认图标
        int icon = android.R.drawable.ic_menu_edit;

        // 创建实体类
        ExpenseEntity expense = new ExpenseEntity(
                category,
                amount,
                currentDate,
                isIncome,
                icon,
                note
        );

        // 插入数据库
        repository.insert(expense);
    }

    // 对外暴露的LiveData（方法名必须和MainActivity完全一致！）
    public LiveData<List<ExpenseEntity>> getExpenses() {
        return allExpenses;
    }

    public LiveData<String> getTotalAmountText() {
        return totalAmountText;
    }

    public LiveData<String> getTotalIncomeText() {
        return totalIncomeText;
    }

    public LiveData<String> getTotalExpenseText() {
        return totalExpenseText;
    }

    public LiveData<String> getLatestExpenseText() {
        return latestExpenseText;
    }
}