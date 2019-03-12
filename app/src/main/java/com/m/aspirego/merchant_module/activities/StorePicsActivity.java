package com.m.aspirego.merchant_module.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.m.aspirego.LoginActivity;
import com.m.aspirego.R;
import com.m.aspirego.helperclasses.ConnectionDetector;
import com.m.aspirego.helperclasses.SessionManagement;
import com.m.aspirego.merchant_module.adapters.StorePicsAdapter;
import com.m.aspirego.merchant_module.models.MLogin;
import com.m.aspirego.merchant_module.models.MerchantPhotoUpdateResponse;
import com.m.aspirego.merchant_module.models.MerchantPhotosResponseModel;
import com.m.aspirego.merchant_module.models.MerchantStoreUpdateModel;
import com.m.aspirego.merchant_module.models.Merchantphoto;
import com.m.aspirego.merchant_module.presenter.RetrofitMerchantApis;
import com.m.aspirego.user_module.models.CategoryModel;
import com.m.aspirego.user_module.presenter.ApiUrls;
import com.m.aspirego.user_module.presenter.RetrofitApis;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class StorePicsActivity extends AppCompatActivity implements StorePicsAdapter.ReplaceImageListener{
    @BindView(R.id.store_pics)
    RecyclerView store_pics;
    @BindView(R.id.back_btn)
    ImageView back_btn;
    List<Merchantphoto> paths;
    StorePicsAdapter storePicsAdapter;
    boolean isNewUpload;

    private Dialog dialog;
    private ConnectionDetector connectionDetector;
    SessionManagement sessionManagement;
    private String merchant_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_pics);
        ButterKnife.bind(this);
        store_pics.setLayoutManager(new GridLayoutManager(this,2));
        paths=new ArrayList<>();
        storePicsAdapter=new StorePicsAdapter(this,paths);
        storePicsAdapter.setListener(this);
        store_pics.setAdapter(storePicsAdapter);

        dialog = new Dialog(StorePicsActivity.this,
                android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.loading);
        dialog.setCancelable(false);
        connectionDetector=new ConnectionDetector(StorePicsActivity.this);
        sessionManagement= SessionManagement.getSession(StorePicsActivity.this);
        merchant_id=sessionManagement.getValueFromPreference(SessionManagement.USERID);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if(connectionDetector.isConnectingToInternet())
            merchantphotosSevice(merchant_id);
        else
            callToast("You've no internet connection. Please try again.");
    }


    public void upLoadPic(View view) {
        if(paths!=null&&paths.size()<6) {
            if (!checkingPermissionAreEnabledOrNot())
                requestMultiplePermission();
            else {
                isNewUpload=true;
                picGalleryImage();
            }
        }
        else{
        callToast("Please replace one of your uploads");
        }
    }
    public boolean checkingPermissionAreEnabledOrNot() {
        int camera = ContextCompat.checkSelfPermission(this, CAMERA);
        int write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
        return camera == PackageManager.PERMISSION_GRANTED && read==PackageManager.PERMISSION_GRANTED;
    }
    private void requestMultiplePermission() {

        ActivityCompat.requestPermissions(this, new String[]
                {
                        CAMERA,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                }, 100);

    }

    private void picGalleryImage()
    {
        CropImage.activity()
                .start(this);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                String path=resultUri.getPath();
                Log.e("path is","path "+path);
                if(isNewUpload) {
                    if (paths.size() < 6) {
                        Merchantphoto merchantphoto=new Merchantphoto();
                        merchantphoto.setId("");
                        merchantphoto.setTempFile(true);
                        merchantphoto.setImage(path);
                        paths.add(merchantphoto);
                        storePicsAdapter.notifyDataSetChanged();
                        if(connectionDetector.isConnectingToInternet())
                           uploadmerchantphoto(paths.size()-1,merchant_id,path);
                        else
                            callToast("You've no internet connection. Please try again.");
                    }
                }
                else {
                    storePicsAdapter.replaceItem(path, position);
                    if(connectionDetector.isConnectingToInternet())
                        updatemerchantphoto(position,paths.get(position).getId(),path);
                    else
                        callToast("You've no internet connection. Please try again.");
                }
                // uploadProfilepic(path);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
    private void callToast(String msg)
    {
        Toast.makeText(StorePicsActivity.this,msg,Toast.LENGTH_SHORT).show();
    }

    int position;
    @Override
    public void onPicReplaceEvent(int position) {
        this.position=position;
        if (!checkingPermissionAreEnabledOrNot())
            requestMultiplePermission();
        else {
            picGalleryImage();
            isNewUpload=false;
        }
    }

    @Override
    public void onPicDeleteEvent(int position) {
        if(paths.get(position).getId().trim().length()>0)
          displayDeleteDialog(position);
        else
            callToast("This image is not uploaded. Please refresh this page and try again");
    }


    private void displayDeleteDialog(final int position)
    {
        final Dialog dialog=new Dialog(StorePicsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.camera_gallery_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        RelativeLayout layout_yes=(RelativeLayout) dialog.findViewById(R.id.layout_yes);
        RelativeLayout layout_no=(RelativeLayout)dialog.findViewById(R.id.layout_no);
        TextView tv_title=(TextView)dialog.findViewById(R.id.tv_title);
        tv_title.setText("Are you sure you want to Delete this photo?");
        TextView tv_no=(TextView)dialog.findViewById(R.id.tv_no);
        tv_no.setText("No");
        TextView tv_yes=(TextView)dialog.findViewById(R.id.tv_yes);
        tv_yes.setText("Yes");

        layout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                if(connectionDetector.isConnectingToInternet())
                    deleteMerchantphotos(position,paths.get(position).getId());
                else
                    callToast("You've no internet connection. Please try again.");
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
    private void merchantphotosSevice(String merchant_id) {
        Log.e("merchant id",""+merchant_id);
        dialog.show();
        Call<MerchantPhotosResponseModel> call = RetrofitMerchantApis.Factory.create(this).merchantphotos(merchant_id);
        call.enqueue(new Callback<MerchantPhotosResponseModel>() {
            @Override
            public void onResponse(Call<MerchantPhotosResponseModel> call, Response<MerchantPhotosResponseModel> response) {
                dialog.dismiss();
                MerchantPhotosResponseModel model=response.body();
                if(model!=null)
                {
                    if(model.getStatus()==1)
                    {
                        Log.e("merchant id",""+model.getMerchantphotos().size());
                      if(model.getMerchantphotos()!=null && model.getMerchantphotos().size()>0)
                          paths.addAll(model.getMerchantphotos());
                      for (int i=0;i<paths.size();i++)
                          paths.get(i).setTempFile(false);
                      storePicsAdapter.notifyDataSetChanged();
                    }
                    else {
                        callToast(model.getResult());
                    }
                }
            }

            @Override
            public void onFailure(Call<MerchantPhotosResponseModel> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }

    private void uploadmerchantphoto(final int position, String merchant_id, String image_path){
        MultipartBody.Part fileToUpload=null;
        final File file=new File(image_path);
        if(file.exists()) {
            RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
        }
        RequestBody mid = RequestBody.create(MediaType.parse("text/plain"),merchant_id);
        Call<MLogin> call= RetrofitMerchantApis.Factory.create(this).uploadmerchantphoto(fileToUpload,mid);
        dialog.show();
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model.getStatus()==1){
                    paths.get(position).setTempFile(false);
                    paths.get(position).setImage(model.getImage());
                    paths.get(position).setId(""+model.getId());
                    storePicsAdapter.notifyDataSetChanged();
                }
                else {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                Log.e("add offer onFailure",""+call.toString());
            }
        });
    }

    private void updatemerchantphoto(final int position,String id,String image_path){
        MultipartBody.Part fileToUpload=null;
        final File file=new File(image_path);
        if(file.exists()) {
            RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            fileToUpload = MultipartBody.Part.createFormData("image", file.getName(), mFile);
        }
        RequestBody photo_id = RequestBody.create(MediaType.parse("text/plain"),id);
        Call<MerchantPhotoUpdateResponse> call= RetrofitMerchantApis.Factory.create(this).updatemerchantphoto(fileToUpload,photo_id);
        dialog.show();
        call.enqueue(new Callback<MerchantPhotoUpdateResponse>() {
            @Override
            public void onResponse(Call<MerchantPhotoUpdateResponse> call, Response<MerchantPhotoUpdateResponse> response) {
                dialog.dismiss();
                MerchantPhotoUpdateResponse model=response.body();
                if(model.getStatus()==1){
                    paths.get(position).setTempFile(false);
                    paths.get(position).setImage(model.getImage());
                    paths.get(position).setId(model.getId());
                    storePicsAdapter.notifyDataSetChanged();
                }
                else {
                    callToast(model.getResult());
                }
            }

            @Override
            public void onFailure(Call<MerchantPhotoUpdateResponse> call, Throwable t) {
                dialog.dismiss();
                Log.e("add offer onFailure",""+call.toString());
            }
        });
    }

    private void deleteMerchantphotos(final int position, String id) {
        dialog.show();
        Call<MLogin> call = RetrofitMerchantApis.Factory.create(this).deletemerchantphoto(id);
        call.enqueue(new Callback<MLogin>() {
            @Override
            public void onResponse(Call<MLogin> call, Response<MLogin> response) {
                dialog.dismiss();
                MLogin model=response.body();
                if(model.getStatus()==1)
                {
                  paths.remove(position);
                  storePicsAdapter.notifyDataSetChanged();
                }
                else
                    callToast(model.getResult());
            }

            @Override
            public void onFailure(Call<MLogin> call, Throwable t) {
                dialog.dismiss();
                callToast(t.getMessage());
            }
        });
    }
}
