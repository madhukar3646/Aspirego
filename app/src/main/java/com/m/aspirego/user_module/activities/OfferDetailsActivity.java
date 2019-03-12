package com.m.aspirego.user_module.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.R;
import com.m.aspirego.user_module.models.Offer;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OfferDetailsActivity extends AppCompatActivity {

    private Offer offer;
    @BindView(R.id.offer_image)
    ImageView offer_image;

    @BindView(R.id.offer_title)
    TextView offer_title;

    @BindView(R.id.offer_address)
    TextView offer_address;

    @BindView(R.id.offer_org_price)
    TextView offer_org_price;

    @BindView(R.id.offer_price)
    TextView offer_price;

    @BindView(R.id.offer_percent)
    TextView offer_percent;

    @BindView(R.id.offer_validity)
    TextView offer_validity;

    @BindView(R.id.back_btn)
    ImageView back_btn;

    @BindView(R.id.iv_call)
    ImageView iv_call;
    private String str_merchantmobilenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        offer = (Offer) getIntent().getParcelableExtra("offer");
        str_merchantmobilenumber=offer.getMobile_number();
        offer_address.setText(offer.getAddress());
        offer_title.setText(offer.getOfferName());
        offer_org_price.setText("Orgilan price : "+getString(R.string.rs)+" "+offer.getPrice());
        offer_price.setText(getString(R.string.rs)+" "+offer.getOfferPrice());
        offer_percent.setText(offer.getDiscount()+" % "+"off");
        offer_validity.setText("offer valid from "+offer.getValidFrom()+" to "+offer.getValidTo());

        Picasso.with(getApplicationContext()).load(ApiUrls.OFFERS_IMAGEPATH+offer.getImage()).placeholder(R.mipmap.logo_icon)
                .error(R.mipmap.logo_icon)
                .into(offer_image);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(str_merchantmobilenumber!=null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + str_merchantmobilenumber));
                    startActivity(intent);
                }
                else
                    callToast("Mobile number not available  right now.");
            }
        });
    }

    private void callToast(String msg)
    {
        Toast.makeText(OfferDetailsActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
