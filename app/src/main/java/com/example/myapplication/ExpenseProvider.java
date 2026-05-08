package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.example.myapplication.database.AppDatabase;
import com.example.myapplication.database.ExpenseEntity;

public class ExpenseProvider extends ContentProvider {

    private static final String TAG = "ExpenseProvider";
    private static final int ALL_EXPENSES = 1;

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        matcher.addURI(ExpenseContract.AUTHORITY, "expenses", ALL_EXPENSES);
    }

    private AppDatabase db;

    @Override
    public boolean onCreate() {
        db = AppDatabase.getInstance(getContext());
        return true;
    }

    // 查询：返回所有账目（教学演示）
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        int code = matcher.match(uri);
        if (code == ALL_EXPENSES) {
            new Thread(() -> {
                int size = db.expenseDao().getAllExpensesSync().size();
                Log.d(TAG, "Provider 查询总数：" + size);
            }).start();
        }

        return null;
    }

    // 插入：通过 ContentProvider 添加账目
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        int code = matcher.match(uri);
        if (code == ALL_EXPENSES) {
            double amount = values.getAsDouble("amount");
            String category = values.getAsString("category");
            String note = values.getAsString("note");
            String date = values.getAsString("date");
            boolean isIncome = values.getAsBoolean("isIncome");

            // 直接用构造方法创建（适配你的实体类）
            ExpenseEntity entity = new ExpenseEntity(
                    category,
                    amount,
                    date,
                    isIncome,
                    0,
                    note
            );

            new Thread(() -> db.expenseDao().insert(entity)).start();
            return ExpenseContract.CONTENT_URI;
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}