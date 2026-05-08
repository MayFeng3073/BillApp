package com.example.myapplication;

import android.net.Uri;
import android.provider.BaseColumns;

public final class ExpenseContract {

    public static final String AUTHORITY = "com.example.myapplication.provider";
    public static final String PATH_EXPENSES = "expenses";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_URI, PATH_EXPENSES);

    public static final class ExpenseEntry implements BaseColumns {
        public static final String TABLE = "expense_table";
        public static final String AMOUNT = "amount";
        public static final String CATEGORY = "category";
        public static final String NOTE = "note";
        public static final String DATE = "date";
        public static final String IS_INCOME = "isIncome";
    }
}