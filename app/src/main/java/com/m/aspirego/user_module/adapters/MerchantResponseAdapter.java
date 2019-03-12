package com.m.aspirego.user_module.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.user_module.models.MerchantResponseModel;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantResponseAdapter extends RecyclerView.Adapter <MerchantResponseAdapter.MyViewHolder>{
    List<MerchantResponseModel.Requirements> requirements;
    Context context;
    String old_imagepath;

    public MerchantResponseAdapter(Context context, String old_imagepath,List<MerchantResponseModel.Requirements> requirements) {
        this.requirements = requirements;
        this.context = context;
        this.old_imagepath=old_imagepath;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.replay_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        MerchantResponseModel.Requirements model = requirements.get(pos);
        String path;
        if(model.getImage()==null || model.getImage().equalsIgnoreCase("null") || model.getImage().trim().length()==0)
            path=old_imagepath;
        else
            path=model.getImage();
        Picasso.with(context).load(MerchantApiUrls.REQUIREMENTS_BASEPATH+path).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(holder.iv_productimage);
        holder.tv_productname.setText(model.getProductName());
        holder.tv_productprice.setText("Rs."+model.getPrice());
        holder.tv_productprice.setPaintFlags(holder.tv_productprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tv_offerprice.setText("Rs."+model.getOfferPrice());
        holder.tv_producttype.setText(model.getProductType());
        holder.tv_deliveryoption.setText("Delivery Option "+model.getDeliveryOption());
        holder.tv_email.setText(model.getEmail());
        holder.tv_mobile.setText(model.getStoreNumber());
        holder.tv_merchantname.setText(model.getMerchantName());
    }

    @Override
    public int getItemCount() {
        if (requirements ==null)
        return 0;

        return requirements.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_producttype)
        TextView tv_producttype;
        @BindView(R.id.iv_productimage)
        ImageView iv_productimage;
        @BindView(R.id.tv_productname)
        TextView tv_productname;
        @BindView(R.id.tv_productprice)
        TextView tv_productprice;
        @BindView(R.id.tv_offerprice)
        TextView tv_offerprice;
        @BindView(R.id.tv_deliveryoption)
        TextView tv_deliveryoption;
        @BindView(R.id.tv_email)
        TextView tv_email;
        @BindView(R.id.tv_mobile)
        TextView tv_mobile;
        @BindView(R.id.tv_merchantname)
        TextView tv_merchantname;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
}
