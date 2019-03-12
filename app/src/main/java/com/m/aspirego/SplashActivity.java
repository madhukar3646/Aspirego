package com.m.aspirego;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.m.aspirego.helperclasses.GPSTracker;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.activities.MerchantHomeActivity;
import com.m.aspirego.user_module.activities.HomeActivity;

public class SplashActivity extends AppCompatActivity {

    Handler handler;
    private SessionManagement sessionManagement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        handler=new Handler();
        handler.postDelayed(th,2000);

    }

    Runnable th=new Runnable() {
        @Override
        public void run() {

             sessionManagement= SessionManagement.getSession(SplashActivity.this);
            if(sessionManagement.getBooleanValueFromPreference(SessionManagement.ISLOGIN))
            {
                if(sessionManagement.getUserType()==SessionManagement.AS_USER)
                {
                    Intent homeintent=new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(homeintent);
                }else if(sessionManagement.getUserType()==SessionManagement.AS_MERCHANT){
                    Intent homeintent=new Intent(SplashActivity.this,MerchantHomeActivity.class);
                    startActivity(homeintent);
                }

                finish();
            }
            else {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            handler.removeCallbacks(th);
        }
    };
}
