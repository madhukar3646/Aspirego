package com.m.aspirego.merchant_module.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.m.aspirego.R;
import com.m.aspirego.merchant_module.models.Merchantphoto;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class StorePicsAdapter  extends RecyclerView.Adapter<StorePicsAdapter.ViewHolder> {
    int src_width;
    Context context;
    List<Merchantphoto> imageurls;
    ReplaceImageListener listener=null;

    public StorePicsAdapter(Context context, List<Merchantphoto> imageurls) {
        this(context);
        this.imageurls = imageurls;
    }

    public StorePicsAdapter(Context context) {
          this.context = context;
          src_width=context.getResources().getDisplayMetrics().widthPixels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int pos) {
       viewHolder.iv_merchantphoto.setImageResource(R.mipmap.logo_icon);
         Merchantphoto merchantphoto=imageurls.get(pos);
         if(merchantphoto.isTempFile())
         {
             Picasso.with(context).load(new File(merchantphoto.getImage())).placeholder(R.mipmap.logo_icon)
                     .error(R.mipmap.logo_icon)
                     .into(viewHolder.iv_merchantphoto);
         }
         else {
             Picasso.with(context).load(ApiUrls.Merchant_Photos + merchantphoto.getImage()).placeholder(R.mipmap.logo_icon)
                     .error(R.mipmap.logo_icon)
                     .into(viewHolder.iv_merchantphoto);
         }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener!=null)
                        listener.onPicReplaceEvent(pos);
                }
            });

          viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
              @Override
              public boolean onLongClick(View view) {
                  if (listener!=null)
                      listener.onPicDeleteEvent(pos);
                  return false;
              }
          });

    }

    public void setListener(ReplaceImageListener listener) {
        this.listener = listener;
    }

    public void replaceItem(String path, int pos){
        if(imageurls!=null&&imageurls.size()>pos){
            imageurls.get(pos).setImage(path);
            imageurls.get(pos).setTempFile(true);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {

        if (imageurls==null)
        return 0;

        return imageurls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView iv_merchantphoto;
        public ViewHolder(View itemView)
        {
            super(itemView);
            iv_merchantphoto=(ImageView) itemView.findViewById(R.id.iv_merchantphoto);
            iv_merchantphoto.getLayoutParams().height=src_width/3;
            iv_merchantphoto.getLayoutParams().width=src_width/3;
        }
    }
    public interface ReplaceImageListener{
        void onPicReplaceEvent(int position);
        void onPicDeleteEvent(int position);
    }
}
