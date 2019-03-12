package com.m.aspirego.merchant_module.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.m.aspirego.R;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.fragments.Loanoffers_fragment;
import com.m.aspirego.merchant_module.fragments.Offers_fragment;
import com.m.aspirego.merchant_module.fragments.Products_fragment;
import com.m.aspirego.merchant_module.fragments.Profile_fragment;
import com.m.aspirego.merchant_module.fragments.Requirement_fragment;
import com.m.aspirego.user_module.activities.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MerchantHomeActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.layout_requirements)
    LinearLayout layout_requirements;
    @BindView(R.id.layout_products)
    LinearLayout layout_products;
    @BindView(R.id.layout_loan_offers)
    LinearLayout layout_loan_offers;
    @BindView(R.id.layout_offers)
    LinearLayout layout_offers;
    @BindView(R.id.layout_profile)
    LinearLayout layout_profile;

    @BindView(R.id.iv_requirements)
    ImageView iv_requirements;
    @BindView(R.id.iv_products)
    ImageView iv_products;
    @BindView(R.id.iv_loan_offers)
    ImageView iv_loan_offers;

    @BindView(R.id.iv_offers)
    ImageView iv_offers;
    @BindView(R.id.iv_profile)
    ImageView iv_profile;

    @BindView(R.id.tv_requirements)
    TextView tv_requirements;
    @BindView(R.id.tv_products)
    TextView tv_products;
    @BindView(R.id.tv_loan_offers)
    TextView tv_loan_offers;
    @BindView(R.id.tv_offers)
    TextView tv_offers;
    @BindView(R.id.tv_profile)
    TextView tv_profile;
    SessionManagement sessionManagement;
    private FragmentTransaction ft;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private String merchantid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_home);
        ButterKnife.bind(this);
        init();
    }

    private void init()
    {
        layout_requirements.setOnClickListener(this);
        layout_loan_offers.setOnClickListener(this);
        layout_offers.setOnClickListener(this);
        layout_profile.setOnClickListener(this);
        layout_products.setOnClickListener(this);
        sessionManagement= SessionManagement.getSession(this);
        merchantid=sessionManagement.getValueFromPreference(SessionManagement.USERID);
        Log.e("m home",""+merchantid);

        fragmentManager = getSupportFragmentManager();
        ft = fragmentManager.beginTransaction();
        fragment=new Requirement_fragment();
        ft.add(R.id.layout_framefor_fragment,fragment);
        ft.commit();
        setClickedFocus(layout_requirements);
    }

    private void setClickedFocus(View  action)
    {
        layout_requirements.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_loan_offers.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_offers.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_profile.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));
        layout_products.setBackgroundColor(getResources().getColor(R.color.colorHomebottomlayoutbg));

        iv_requirements.setImageResource(R.mipmap.aroundme_black);
        iv_loan_offers.setImageResource(R.mipmap.upload_black);
        iv_offers.setImageResource(R.mipmap.offer_black);
        iv_profile.setImageResource(R.mipmap.profile_black);
        iv_products.setImageResource(R.mipmap.feed_black);

        tv_requirements.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_loan_offers.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_offers.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_profile.setTextColor(getResources().getColor(R.color.textcolorblack));
        tv_products.setTextColor(getResources().getColor(R.color.textcolorblack));
        switch (action.getId()){
            case R.id.layout_requirements:

                layout_requirements.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_requirements.setImageResource(R.mipmap.aroundme_white);
                tv_requirements.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.layout_loan_offers:
                layout_loan_offers.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_loan_offers.setImageResource(R.mipmap.upload_white);
                tv_loan_offers.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.layout_offers:
                layout_offers.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_offers.setImageResource(R.mipmap.offer_white);
                tv_offers.setTextColor(Color.parseColor("#ffffff"));
                break;
            case R.id.layout_profile:
                layout_profile.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_profile.setImageResource(R.mipmap.profile_white);
                tv_profile.setTextColor(Color.parseColor("#ffffff"));
                break;

            case R.id.layout_products:
                layout_products.setBackgroundColor(getResources().getColor(R.color.colorbottomclickedtbg));
                iv_products.setImageResource(R.mipmap.feed_white);
                tv_products.setTextColor(Color.parseColor("#ffffff"));
                break;
        }

    }

    @Override
    public void onBackPressed() {
        displayExitDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_requirements:
                changeFragment(new Requirement_fragment());
                break;
            case R.id.layout_loan_offers:
                changeFragment(new Loanoffers_fragment());
                break;
            case R.id.layout_offers:
                changeFragment(new Offers_fragment());
                break;
            case R.id.layout_profile:
                changeFragment(new Profile_fragment());
                break;
            case R.id.layout_products:
                changeFragment(new Products_fragment());
                break;
        }
        setClickedFocus(view);
    }

    private void changeFragment(Fragment fragment)
    {
        this.fragment=fragment;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.layout_framefor_fragment, fragment);
        ft.commit();
        invalidateOptionsMenu();
    }

    private void displayExitDialog()
    {
        final Dialog dialog=new Dialog(MerchantHomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_gallery_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        RelativeLayout layout_yes=(RelativeLayout) dialog.findViewById(R.id.layout_yes);
        RelativeLayout layout_no=(RelativeLayout)dialog.findViewById(R.id.layout_no);
        TextView tv_title=(TextView)dialog.findViewById(R.id.tv_title);
        tv_title.setText("Are you sure you want to exit from app?");
        TextView tv_no=(TextView)dialog.findViewById(R.id.tv_no);
        tv_no.setText("No");
        TextView tv_yes=(TextView)dialog.findViewById(R.id.tv_yes);
        tv_yes.setText("Yes");

        layout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                finish();
                finishAffinity();
            }
        });

        layout_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

}
