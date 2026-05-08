package com.example.myapplication;

import android.app.Application;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.ExpenseDao;
import com.example.myapplication.database.ExpenseEntity;
import java.util.List;

// 仓库类：连接ViewModel和数据库
public class ExpenseRepository {
    private final ExpenseDao expenseDao;
    private final LiveData<List<ExpenseEntity>> allExpenses;
    private final LiveData<Double> totalAmount;
    private final LiveData<Double> totalIncome;
    private final LiveData<Double> totalExpense;

    // 构造方法
    public ExpenseRepository(Application application) {
        AppDatabase database = AppDatabase.getInstance(application);
        expenseDao = database.expenseDao();
        allExpenses = expenseDao.getAllExpenses();
        totalAmount = expenseDao.getTotalAmount();
        totalIncome = expenseDao.getTotalIncome();
        totalExpense = expenseDao.getTotalExpense();
    }

    // 插入数据（异步线程）
    public void insert(ExpenseEntity expense) {
        new InsertExpenseAsyncTask(expenseDao).execute(expense);
    }

    // 获取所有数据
    public LiveData<List<ExpenseEntity>> getAllExpenses() {
        return allExpenses;
    }

    public LiveData<Double> getTotalAmount() {
        return totalAmount;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getTotalExpense() {
        return totalExpense;
    }

    // 异步插入任务
    private static class InsertExpenseAsyncTask extends AsyncTask<ExpenseEntity, Void, Void> {
        private final ExpenseDao expenseDao;

        private InsertExpenseAsyncTask(ExpenseDao expenseDao) {
            this.expenseDao = expenseDao;
        }

        @Override
        protected Void doInBackground(ExpenseEntity... expenses) {
            expenseDao.insert(expenses[0]);
            return null;
        }
    }
}