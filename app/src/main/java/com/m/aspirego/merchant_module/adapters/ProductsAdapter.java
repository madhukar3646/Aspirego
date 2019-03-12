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
import com.m.aspirego.merchant_module.models.ProductListResponse;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.MyViewHolder> {
  List<ProductListResponse.Product> productModelList=null;
  Context context;
  ProductsDeleteListerner listerner;

    public ProductsAdapter(Context context, List<ProductListResponse.Product> productModelList) {
        this.productModelList = productModelList;
        this.context = context;
    }

    public void setDeleteListener(ProductsDeleteListerner listener)
    {
        this.listerner=listener;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addproduct_model,viewGroup,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int pos) {

        final ProductListResponse.Product model=productModelList.get(pos);
        holder.tv_name.setText(model.getProductName());
        holder.btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listerner!=null){
                    listerner.onProductsDelete(productModelList.get(pos));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(productModelList==null)
        return 0;

        return productModelList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_name)
        TextView tv_name;
        @BindView(R.id.btn_minus)
        ImageView btn_minus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public interface ProductsDeleteListerner{
        void onProductsDelete(ProductListResponse.Product productModel);
    }
}
