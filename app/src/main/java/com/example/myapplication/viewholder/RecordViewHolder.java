package com.example.myapplication.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;

public class RecordViewHolder extends RecyclerView.ViewHolder {
    // 控件声明
    ImageView iv_icon;
    TextView tv_type_name;
    TextView tv_amount;
    TextView tv_category;
    TextView tv_date;
    TextView tv_note;

    public RecordViewHolder(@NonNull View itemView) {
        super(itemView);
        // 绑定item_record.xml中的ID
        iv_icon = itemView.findViewById(R.id.iv_icon);
        tv_type_name = itemView.findViewById(R.id.tv_type_name);
        tv_amount = itemView.findViewById(R.id.tv_amount);
        tv_category = itemView.findViewById(R.id.tv_category);
        tv_date = itemView.findViewById(R.id.tv_date);
        tv_note = itemView.findViewById(R.id.tv_note);
    }

    // 绑定数据
    // 绑定数据
    public void bind(Expense expense) {

        // ===================== 【核心修复：只有测试记录显示数据库图标】 =====================
        if ("Provider测试".equals(expense.getTypeName())) {
            // 换一个所有版本都能用的系统图标
            iv_icon.setImageResource(android.R.drawable.ic_menu_manage);
        } else {
            // 正常记录 → 显示原来的图标（完全不变）
            iv_icon.setImageResource(expense.getIconResId());
        }
        // ====================================================================================

        tv_type_name.setText(expense.getTypeName());

        // ✅ 直接设置double，不调用任何自定义方法
        tv_amount.setText(String.valueOf(expense.getAmount()));

        tv_category.setText(expense.isIncome() ? "收入" : "支出");
        // 设置颜色
        int color = expense.isIncome() ?
                itemView.getContext().getColor(R.color.income) :
                itemView.getContext().getColor(R.color.expense);
        tv_amount.setTextColor(color); // 这里是setTextColor，不是setText！
        tv_category.setTextColor(color);

        // 日期处理
        String date = expense.getDate();
        if (date != null && date.length() > 10) {
            tv_date.setText(date.substring(0, 10));
        } else {
            tv_date.setText(date);
        }

        // 备注
        if (expense.getNote() != null && !expense.getNote().isEmpty()) {
            tv_note.setText(expense.getNote());
            tv_note.setVisibility(View.VISIBLE);
        } else {
            tv_note.setVisibility(View.GONE);
        }
    }
}