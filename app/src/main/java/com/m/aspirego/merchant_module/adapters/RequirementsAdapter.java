package com.m.aspirego.merchant_module.adapters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.merchant_module.activities.MerchantReplyActivity;
import com.m.aspirego.merchant_module.models.RequirementsModel;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.user_module.models.TagsModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by admin on 4/20/2017.
 */

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.MyViewHolder>
{
    private Activity context;
    private List<RequirementsModel.Requirement> requirements;
    private List<RequirementsModel.MerchantTag> merchantTags=new ArrayList<RequirementsModel.MerchantTag>();

    public RequirementsAdapter(Activity context, List<RequirementsModel.Requirement> requirements,List<RequirementsModel.MerchantTag> merchantTags)
    {
        this.context=context;
        this.requirements=requirements;
        this.merchantTags=merchantTags;
    }

    @Override
    public RequirementsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.requirement_model, parent, false);

        return new RequirementsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RequirementsAdapter.MyViewHolder holder, final int position)
    {
        final RequirementsAdapter.MyViewHolder myViewHolder=holder;
        RequirementsModel.Requirement model=requirements.get(position);
        holder.tv_description.setText(model.getTitle()+", "+model.getDescription());

       Picasso.with(context).load(MerchantApiUrls.REQUIREMENTS_BASEPATH+model.getPhoto()).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(holder.iv_productimage);

        ArrayList<String> usertags=new ArrayList<>();
       for(String tag:model.getTags())
         usertags.add(tag);

        holder.layout_tag1.setVisibility(View.GONE);
        holder.layout_tag2.setVisibility(View.GONE);
        holder.layout_tag3.setVisibility(View.GONE);
        holder.layout_tag4.setVisibility(View.GONE);
        holder.layout_tag5.setVisibility(View.GONE);
        holder.layout_tag6.setVisibility(View.GONE);

       for (int i=0;i<merchantTags.size();i++)
       {
           if(usertags.contains(merchantTags.get(i).getTagName()))
           {
             if(i==0)
             {
                  holder.layout_tag1.setVisibility(View.VISIBLE);
                  holder.layout_tag1.setBackgroundResource(R.drawable.round_rect_orange);
                  holder.tv_tag1.setText(merchantTags.get(i).getTagName());
                  holder.tv_tag1.setTextColor(Color.WHITE);
             }
             else if(i==1)
             {
                 holder.layout_tag2.setVisibility(View.VISIBLE);
                 holder.layout_tag2.setBackgroundResource(R.drawable.round_rect_orange);
                 holder.tv_tag2.setText(merchantTags.get(i).getTagName());
                 holder.tv_tag2.setTextColor(Color.WHITE);
             }
             else if(i==2)
             {
                 holder.layout_tag3.setVisibility(View.VISIBLE);
                 holder.layout_tag3.setBackgroundResource(R.drawable.round_rect_orange);
                 holder.tv_tag3.setText(merchantTags.get(i).getTagName());
                 holder.tv_tag3.setTextColor(Color.WHITE);
             }
             else if(i==3)
             {
                 holder.layout_tag4.setVisibility(View.VISIBLE);
                 holder.layout_tag4.setBackgroundResource(R.drawable.round_rect_orange);
                 holder.tv_tag4.setText(merchantTags.get(i).getTagName());
                 holder.tv_tag4.setTextColor(Color.WHITE);
             }
             else if(i==4)
             {
                 holder.layout_tag5.setVisibility(View.VISIBLE);
                 holder.layout_tag5.setBackgroundResource(R.drawable.round_rect_orange);
                 holder.tv_tag5.setText(merchantTags.get(i).getTagName());
                 holder.tv_tag5.setTextColor(Color.WHITE);
             }
             else if(i==5)
             {
                 holder.layout_tag6.setVisibility(View.VISIBLE);
                 holder.layout_tag6.setBackgroundResource(R.drawable.round_rect_orange);
                 holder.tv_tag6.setText(merchantTags.get(i).getTagName());
                 holder.tv_tag6.setTextColor(Color.WHITE);
             }
           }
           else {
               if(i==0)
               {
                   holder.layout_tag1.setVisibility(View.VISIBLE);
                   holder.layout_tag1.setBackgroundResource(R.drawable.round_rect_gray);
                   holder.tv_tag1.setText(merchantTags.get(i).getTagName());
                   holder.tv_tag1.setTextColor(Color.BLACK);
               }
               else if(i==1)
               {
                   holder.layout_tag2.setVisibility(View.VISIBLE);
                   holder.layout_tag2.setBackgroundResource(R.drawable.round_rect_gray);
                   holder.tv_tag2.setText(merchantTags.get(i).getTagName());
                   holder.tv_tag2.setTextColor(Color.BLACK);
               }
               else if(i==2)
               {
                   holder.layout_tag3.setVisibility(View.VISIBLE);
                   holder.layout_tag3.setBackgroundResource(R.drawable.round_rect_gray);
                   holder.tv_tag3.setText(merchantTags.get(i).getTagName());
                   holder.tv_tag3.setTextColor(Color.BLACK);
               }
               else if(i==3)
               {
                   holder.layout_tag4.setVisibility(View.VISIBLE);
                   holder.layout_tag4.setBackgroundResource(R.drawable.round_rect_gray);
                   holder.tv_tag4.setText(merchantTags.get(i).getTagName());
                   holder.tv_tag4.setTextColor(Color.BLACK);
               }
               else if(i==4)
               {
                   holder.layout_tag5.setVisibility(View.VISIBLE);
                   holder.layout_tag5.setBackgroundResource(R.drawable.round_rect_gray);
                   holder.tv_tag5.setText(merchantTags.get(i).getTagName());
                   holder.tv_tag5.setTextColor(Color.BLACK);
               }
               else if(i==5)
               {
                   holder.layout_tag6.setVisibility(View.VISIBLE);
                   holder.layout_tag6.setBackgroundResource(R.drawable.round_rect_gray);
                   holder.tv_tag6.setText(merchantTags.get(i).getTagName());
                   holder.tv_tag6.setTextColor(Color.BLACK);
               }
           }
       }

       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent=new Intent(context, MerchantReplyActivity.class);
               intent.putExtra("requirementid",requirements.get(position).getId());
               intent.putExtra("name",requirements.get(position).getName());
               intent.putExtra("mobile",requirements.get(position).getMobileNumber());
               intent.putExtra("email",requirements.get(position).getEmail());
               context.startActivity(intent);
           }
       });
    }

    @Override
    public int getItemCount()
    {
        return requirements.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tv_description,tv_tag1,tv_tag2,tv_tag3,tv_tag4,tv_tag5,tv_tag6;
        public RelativeLayout layout_tag1,layout_tag2,layout_tag3,layout_tag4,layout_tag5,layout_tag6;
        public ImageView iv_productimage;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            tv_description=(TextView)itemView.findViewById(R.id.tv_description);
            iv_productimage=(ImageView) itemView.findViewById(R.id.iv_productimage);
            layout_tag1=(RelativeLayout)itemView.findViewById(R.id.layout_tag1);
            layout_tag2=(RelativeLayout)itemView.findViewById(R.id.layout_tag2);
            layout_tag3=(RelativeLayout)itemView.findViewById(R.id.layout_tag3);
            layout_tag4=(RelativeLayout)itemView.findViewById(R.id.layout_tag4);
            layout_tag5=(RelativeLayout)itemView.findViewById(R.id.layout_tag5);
            layout_tag6=(RelativeLayout)itemView.findViewById(R.id.layout_tag6);

            tv_tag1=(TextView)itemView.findViewById(R.id.tv_tag1);
            tv_tag2=(TextView)itemView.findViewById(R.id.tv_tag2);
            tv_tag3=(TextView)itemView.findViewById(R.id.tv_tag3);
            tv_tag4=(TextView)itemView.findViewById(R.id.tv_tag4);
            tv_tag5=(TextView)itemView.findViewById(R.id.tv_tag5);
            tv_tag6=(TextView)itemView.findViewById(R.id.tv_tag6);
        }
    }
}
