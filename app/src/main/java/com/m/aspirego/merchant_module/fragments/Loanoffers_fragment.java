package com.m.aspirego.merchant_module.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.LoginActivity;
import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.presenter.MerchantApiUrls;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Loanoffers_fragment extends Fragment implements View.OnClickListener{

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    @BindView(R.id.et_email)
    EditText et_email;
    @BindView(R.id.et_mobilenumber)
    EditText et_mobilenumber;
    @BindView(R.id.et_amount)
    EditText et_amount;
    @BindView(R.id.layout_submit)
    RelativeLayout layout_submit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        View view=inflater.inflate(R.layout.fragment_loanoffers_fragment, container, false);
        ButterKnife.bind(this,view);
        init(view);
        return view;
    }

    private void init(View view)
    {
        dialog = new Dialog(getActivity(),
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(getActivity());
        sessionManagement= SessionManagement.getSession(getContext());
        layout_submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.layout_submit:
                checkInputValidations();
                break;
        }
    }

    private void checkInputValidations()
    {
       String str_email,str_mobile,str_amount;
       str_email=et_email.getText().toString().trim();
       str_mobile=et_mobilenumber.getText().toString().trim();
       str_amount=et_amount.getText().toString().trim();

       if(str_email.length()==0)
           callToast("Please enter your email address");
       else if(!signupEmail(str_email))
           callToast("Please enter valid email address");
       else if(str_mobile.length()==0)
           callToast("Please enter your mobile number");
       else if(str_mobile.length()<10)
           callToast("Please enter valid mobile number");
       else if(str_amount.length()==0)
           callToast("Please enter amount");
       else{
           if(connectionDetector.isConnectingToInternet())
           {
               LoanApplyService(sessionManagement.getValueFromPreference(SessionManagement.USERID),str_mobile,str_email,str_amount);
           }
           else {
               callToast("You've no internet connection. Please try again.");
           }
       }

    }

    private boolean signupEmail(String email)
    {
        String emailPattern =
                "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void callToast(String msg)
    {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    private void LoanApplyService(final String merchantid,String mobilenumber,String email,String amount)
    {
        dialog.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MerchantApiUrls.BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitMerchantApis service = retrofit.create(RetrofitMerchantApis.class);
        Call<MLogin> call = service.applyloanService(merchantid,mobilenumber,email,amount);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1) {
                        et_amount.setText("");
                        et_email.setText("");
                        et_mobilenumber.setText("");
                        displayConfirmationDialog();
                    }
                    else
                        callToast(model.getResult());
                }
                else {
                    callToast("No data Found");
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });

    }

    private void displayConfirmationDialog()
    {
        final Dialog dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_gallery_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        RelativeLayout layout_yes=(RelativeLayout) dialog.findViewById(R.id.layout_yes);
        RelativeLayout layout_no=(RelativeLayout)dialog.findViewById(R.id.layout_no);
        layout_no.setVisibility(View.GONE);
        TextView tv_title=(TextView)dialog.findViewById(R.id.tv_title);
        tv_title.setText("You have applied loan successfully.");
        TextView tv_no=(TextView)dialog.findViewById(R.id.tv_no);
        tv_no.setText("No");
        TextView tv_yes=(TextView)dialog.findViewById(R.id.tv_yes);
        tv_yes.setText("Ok");

        layout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
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
