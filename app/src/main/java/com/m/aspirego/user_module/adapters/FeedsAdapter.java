package com.m.aspirego.user_module.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.user_module.models.UserRequirementsListResponse;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FeedsAdapter extends RecyclerView.Adapter <FeedsAdapter.MyViewHolder>{
    List<UserRequirementsListResponse.Requirements> requirements;
    Context context;
    ViewFullDetailsListener listener;

    public FeedsAdapter(Context context, List<UserRequirementsListResponse.Requirements> requirements) {
        this.requirements = requirements;
        this.context = context;
    }

    public void setListener(ViewFullDetailsListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.feed_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {
        UserRequirementsListResponse.Requirements model = requirements.get(pos);
        Picasso.with(context).load(MerchantApiUrls.REQUIREMENTS_BASEPATH+requirements.get(pos).getPhoto()).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(holder.image);
        holder.feedName.setText(model.getTitle());
        holder.tv_description.setText(model.getDescription());
        holder.tv_date.setText(model.getCreatedOn());
        holder.viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onViewFullDetails(requirements.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (requirements ==null)
        return 0;

        return requirements.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.feed_name)
        TextView feedName;
        @BindView(R.id.feed_image)
        ImageView image;
        @BindView(R.id.viewall)
        TextView viewall;
        @BindView(R.id.tv_description)
        TextView tv_description;
        @BindView(R.id.tv_date)
        TextView tv_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }
    }
    public interface ViewFullDetailsListener {
        void onViewFullDetails(UserRequirementsListResponse.Requirements feed);
    }
}
