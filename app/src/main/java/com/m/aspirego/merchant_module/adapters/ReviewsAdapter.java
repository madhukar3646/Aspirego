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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    Context context;
    List<ReviewslistResponse.Review> reviewList;

    public ReviewsAdapter(Context context, List<ReviewslistResponse.Review> reviewList) {
        this.context=context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reviews_model, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int pos) {
        ReviewslistResponse.Review model=reviewList.get(pos);
        viewHolder.tv_name.setText(model.getName());
        viewHolder.tv_review.setText(model.getReview());
    }

    @Override
    public int getItemCount() {

        return reviewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView tv_name,tv_review;
        public ViewHolder(View itemView)
        {
            super(itemView);
            tv_name=(TextView) itemView.findViewById(R.id.tv_name);
            tv_review=(TextView) itemView.findViewById(R.id.tv_review);
        }
    }

}
