package com.example.myapplication.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

// Room数据库实体类 → 对应数据库表
@Entity(tableName = "expense_table")
public class ExpenseEntity {
    // 主键 自增长
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String typeName;    // 类型名称
    private double amount;     // 金额
    private String date;       // 日期
    private boolean isIncome;  // 是否收入
    private int iconResId;     // 图标
    private String note;       // 备注

    // 构造函数
    public ExpenseEntity(String typeName, double amount, String date, boolean isIncome, int iconResId, String note) {
        this.typeName = typeName;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
        this.iconResId = iconResId;
        this.note = note;
    }

    // Getter & Setter
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public boolean isIncome() { return isIncome; }
    public void setIncome(boolean income) { isIncome = income; }
    public int getIconResId() { return iconResId; }
    public void setIconResId(int iconResId) { this.iconResId = iconResId; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}