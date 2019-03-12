package com.m.aspirego.user_module.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.merchant_module.models.ProductListResponse;

import java.util.List;

/**
 * Created by admin on 4/20/2017.
 */

public class AutoSearchAdapterDemo extends RecyclerView.Adapter<AutoSearchAdapterDemo.MyViewHolder>
{
    private Activity context;
    private List<ProductListResponse.Product> autoSearchAdapterModelList;
    private AutoSearchClicklistener autoSearchClicklistener;
    public AutoSearchAdapterDemo(Activity context, List<ProductListResponse.Product> autoSearchAdapterModelList, AutoSearchClicklistener autoSearchClicklistener)
    {
        this.context=context;
        this.autoSearchAdapterModelList=autoSearchAdapterModelList;
        this.autoSearchClicklistener=autoSearchClicklistener;
    }

    @Override
    public AutoSearchAdapterDemo.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.autosearch_model, parent, false);

        return new AutoSearchAdapterDemo.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final AutoSearchAdapterDemo.MyViewHolder holder, final int position)
    {
        final AutoSearchAdapterDemo.MyViewHolder myViewHolder=holder;
        final ProductListResponse.Product model=autoSearchAdapterModelList.get(position);
        holder.tv_name.setText(model.getProductName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                autoSearchClicklistener.onAutoSearchClicked(autoSearchAdapterModelList.get(position));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return autoSearchAdapterModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_name;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_name=(TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    public interface AutoSearchClicklistener
    {
        void onAutoSearchClicked(ProductListResponse.Product model);
    }
}
