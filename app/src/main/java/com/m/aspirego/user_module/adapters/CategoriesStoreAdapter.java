package com.m.aspirego.user_module.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.user_module.models.CategoryStoreModel;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by admin on 4/20/2017.
 */

public class CategoriesStoreAdapter extends RecyclerView.Adapter<CategoriesStoreAdapter.MyViewHolder>
{
    private Activity context;
    private List<CategoryStoreModel.Categorystore> categorystores;
    private onStoreClickListener onStoreClickListener;

    public CategoriesStoreAdapter(Activity context,onStoreClickListener onStoreClickListener, List<CategoryStoreModel.Categorystore> categorystores)
    {
        this.context=context;
        this.categorystores=categorystores;
        this.onStoreClickListener=onStoreClickListener;
    }

    @Override
    public CategoriesStoreAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_storemodel, parent, false);

        return new CategoriesStoreAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CategoriesStoreAdapter.MyViewHolder holder, final int position)
    {
        final CategoriesStoreAdapter.MyViewHolder myViewHolder=holder;
        final CategoryStoreModel.Categorystore model=categorystores.get(position);
        String str_distance=model.getDistance();
        if(str_distance==null || str_distance.trim().length()==0 || str_distance.equalsIgnoreCase("null"))
            str_distance="0.0";
        str_distance=String.format("%.2f", Double.parseDouble(str_distance));
        holder.tv_distance.setText(str_distance+" KMS");
        holder.tv_marchantname.setText(model.getMerchantName());
        holder.tv_textonimage.setText(model.getMerchantName());
        holder.tv_state.setText(model.getState());
        if(model.getRating()!=null)
           holder.tv_rating.setText(model.getRating());
        else
            holder.tv_rating.setText("0.0");

        if(model.getMerchantBanner()!=null && model.getMerchantBanner().trim().length()>0 && !model.getMerchantBanner().equalsIgnoreCase("null")) {
            holder.iv_storebanner.setBackgroundResource(R.drawable.roundrect_white);
            Picasso.with(context).load(ApiUrls.MERCHANTBANNERS + model.getMerchantBanner()).placeholder(R.mipmap.logo_icon)
                    .error(R.mipmap.logo_icon)
                    .into(holder.iv_storebanner);
            holder.tv_textonimage.setVisibility(View.GONE);
        }
        else {
            holder.iv_storebanner.setBackgroundResource(R.drawable.roundrect_primary);
            holder.tv_textonimage.setVisibility(View.VISIBLE);
            holder.iv_storebanner.setImageBitmap(null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onStoreClickListener.onStoreClick(categorystores.get(position));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return categorystores.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_marchantname,tv_rating,tv_state,tv_distance,tv_textonimage;
        public ImageView iv_storebanner;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_marchantname=(TextView) itemView.findViewById(R.id.tv_marchantname);
            tv_rating=(TextView) itemView.findViewById(R.id.tv_rating);
            tv_state=(TextView) itemView.findViewById(R.id.tv_state);
            tv_distance=(TextView) itemView.findViewById(R.id.tv_distance);
            iv_storebanner=(ImageView) itemView.findViewById(R.id.iv_storebanner);
            tv_textonimage=(TextView)itemView.findViewById(R.id.tv_textonimage);
            Typeface custom_font = Typeface.createFromAsset(context.getAssets(),  "fonts/PlayfairDisplay-Regular.otf");
            tv_textonimage.setTypeface(custom_font);
        }
    }

    public interface onStoreClickListener
    {
        public void onStoreClick(CategoryStoreModel.Categorystore model);
    }
}
