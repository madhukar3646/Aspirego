package com.m.aspirego.merchant_module.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.user_module.models.CategoryModel;

import java.util.List;

public class CategoryDropDownAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflter;
    private List<CategoryModel.Category> categories;

    public CategoryDropDownAdapter(Context applicationContext, List<CategoryModel.Category> categories) {
        this.context = applicationContext;
        this.categories=categories;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        CategoryModel.Category model=categories.get(position);
        view = inflter.inflate(R.layout.category_dropdown_model, null);
        TextView category = (TextView) view.findViewById(R.id.tv_selectedCategory);
        category.setText(model.getName());
        return view;
    }
}