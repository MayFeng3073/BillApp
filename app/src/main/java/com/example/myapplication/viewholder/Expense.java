package com.example.myapplication.viewholder;

// 页面展示用的数据类（极简版，无任何依赖）
public class Expense {
    private int id;
    private String typeName;
    private double amount;
    private String date;
    private boolean isIncome;
    private int iconResId;
    private String note;

    // 构造方法
    public Expense(int id, String typeName, double amount, String date, boolean isIncome, int iconResId, String note) {
        this.id = id;
        this.typeName = typeName;
        this.amount = amount;
        this.date = date;
        this.isIncome = isIncome;
        this.iconResId = iconResId;
        this.note = note;
    }

    // ✅ 所有Getter必须完全存在（否则ViewHolder绑定会崩溃）
    public int getId() { return id; }
    public String getTypeName() { return typeName; }
    public double getAmount() { return amount; }
    public String getDate() { return date; }
    public boolean isIncome() { return isIncome; }
    public int getIconResId() { return iconResId; }
    public String getNote() { return note; }

    // 移除所有复杂方法，只保留基础Getter
}