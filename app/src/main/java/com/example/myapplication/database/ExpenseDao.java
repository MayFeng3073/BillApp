package com.example.myapplication.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

// 数据库操作接口：增删改查
@Dao
public interface ExpenseDao {
    // 插入记录
    @Insert
    void insert(ExpenseEntity expense);

    // 查询所有记录（倒序）
    @Query("SELECT * FROM expense_table ORDER BY id DESC")
    LiveData<List<ExpenseEntity>> getAllExpenses();

    // 查询总金额
    @Query("SELECT SUM(CASE WHEN isIncome = 1 THEN amount ELSE -amount END) FROM expense_table")
    LiveData<Double> getTotalAmount();

    // 查询总收入
    @Query("SELECT SUM(amount) FROM expense_table WHERE isIncome = 1")
    LiveData<Double> getTotalIncome();

    // 查询总支出
    @Query("SELECT SUM(amount) FROM expense_table WHERE isIncome = 0")
    LiveData<Double> getTotalExpense();

    // 同步查询所有数据（专门给后台服务用）
    @Query("SELECT * FROM expense_table ORDER BY id DESC")
    List<ExpenseEntity> getAllExpensesSync();
}