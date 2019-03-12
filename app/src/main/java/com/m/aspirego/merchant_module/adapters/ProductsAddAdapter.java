package com.m.aspirego.merchant_module.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.merchant_module.models.ReviewslistResponse;

import java.util.ArrayList;
import java.util.List;

public class ProductsAddAdapter extends RecyclerView.Adapter<ProductsAddAdapter.ViewHolder> {

    Context context;
    ArrayList<String> names_list;
    OnDeleteItemListener onDeleteItemListener;

    public ProductsAddAdapter(Context context, OnDeleteItemListener onDeleteItemListener,ArrayList<String> names_list) {
        this.context=context;
        this.names_list = names_list;
        this.onDeleteItemListener=onDeleteItemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.addproduct_model, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int pos) {
        String model=names_list.get(pos);
        viewHolder.tv_name.setText(model);
        viewHolder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              onDeleteItemListener.onDeleteclick(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return names_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_name;
        public ImageView btn_minus;
        public ViewHolder(View itemView)
        {
            super(itemView);
            tv_name=(TextView) itemView.findViewById(R.id.tv_name);
            btn_minus=(ImageView) itemView.findViewById(R.id.btn_minus);
        }
    }

    public interface OnDeleteItemListener
    {
        void onDeleteclick(int pos);
    }
}
