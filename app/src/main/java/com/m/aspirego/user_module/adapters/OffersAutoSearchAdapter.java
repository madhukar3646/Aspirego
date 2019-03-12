package com.m.aspirego.user_module.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.user_module.models.CategoryModel;

import java.util.List;

/**
 * Created by admin on 4/20/2017.
 */

public class OffersAutoSearchAdapter extends RecyclerView.Adapter<OffersAutoSearchAdapter.MyViewHolder>
{
    private Activity context;
    private List<CategoryModel.Category> autoSearchAdapterModelList;
    private AutoSearchClicklistener autoSearchClicklistener;
    public OffersAutoSearchAdapter(Activity context, List<CategoryModel.Category> autoSearchAdapterModelList, AutoSearchClicklistener autoSearchClicklistener)
    {
        this.context=context;
        this.autoSearchAdapterModelList=autoSearchAdapterModelList;
        this.autoSearchClicklistener=autoSearchClicklistener;
    }

    @Override
    public OffersAutoSearchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.autosearch_model, parent, false);

        return new OffersAutoSearchAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OffersAutoSearchAdapter.MyViewHolder holder, final int position)
    {
        final OffersAutoSearchAdapter.MyViewHolder myViewHolder=holder;
        final CategoryModel.Category model=autoSearchAdapterModelList.get(position);
        holder.tv_name.setText(model.getName());
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
        void onAutoSearchClicked(CategoryModel.Category model);
    }
}
