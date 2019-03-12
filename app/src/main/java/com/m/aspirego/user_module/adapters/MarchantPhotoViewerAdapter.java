package com.m.aspirego.user_module.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.m.aspirego.R;
import com.m.aspirego.merchant_module.models.Merchantphoto;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import java.util.List;
/**
 * Created by admin on 4/20/2017.
 */

public class MarchantPhotoViewerAdapter extends RecyclerView.Adapter<MarchantPhotoViewerAdapter.MyViewHolder>
{
    private Activity context;
    private List<Merchantphoto> storeDetails;

    public MarchantPhotoViewerAdapter(Activity context, List<Merchantphoto> storeDetails)
    {
        this.context=context;
        this.storeDetails=storeDetails;
    }

    @Override
    public MarchantPhotoViewerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marchant_photoviewer, parent, false);

        return new MarchantPhotoViewerAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MarchantPhotoViewerAdapter.MyViewHolder holder, final int position)
    {
        final MarchantPhotoViewerAdapter.MyViewHolder myViewHolder=holder;
        Merchantphoto model=storeDetails.get(position);

        Picasso.with(context).load(ApiUrls.Merchant_Photos+model.getImage()).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(holder.iv_merchantphoto);
    }

    @Override
    public int getItemCount()
    {
        return storeDetails.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView iv_merchantphoto;
        public MyViewHolder(View itemView)
        {
            super(itemView);
            iv_merchantphoto=(ImageView) itemView.findViewById(R.id.iv_merchantphoto);
        }
    }
}
