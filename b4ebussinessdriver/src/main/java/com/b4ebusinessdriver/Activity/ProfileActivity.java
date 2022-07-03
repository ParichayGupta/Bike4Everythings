package com.b4ebusinessdriver.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.b4ebusinessdriver.BuildConfig;
import com.b4ebusinessdriver.R;
import com.b4ebusinessdriver.Services.RequestToServer;
import com.b4ebusinessdriver.Utils.AppConstant;
import com.b4ebusinessdriver.Utils.AppPreferences;
import com.b4ebusinessdriver.Utils.CircleImageView;
import com.b4ebusinessdriver.Utils.ProgressDialog;
import com.b4elibrary.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {


    private static final int REQUEST_CAMERA = 11;
    private static final int REQUEST_LOAD_IMAGE = 12;
    private static final int PIC_CROP = 13;

    @BindView(R.id.name)
    EditText name;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.changepassword)
    TextView changepassword;
    @BindView(R.id.profileImage)
    CircleImageView profileImage;
    @BindView(R.id.updateImage)
    ImageView updateImage;
    @BindView(R.id.update)
    Button update;

    String driver_image = "";
    @BindView(R.id.switchActive)
    Switch switchActive;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name.setText(AppPreferences.getUsername(ProfileActivity.this));
        mobile.setText(AppPreferences.getMobileNo(ProfileActivity.this));
        email.setText(AppPreferences.getEmail(ProfileActivity.this));
        password.setText(AppPreferences.getPassword(ProfileActivity.this));
        String image = AppPreferences.getProfilePic(ProfileActivity.this);
        Logger.log("profileimage", image + " >>>>");
        if (!TextUtils.isEmpty(image)) {
            Glide.with(ProfileActivity.this)
                    .asBitmap()
                    .load(AppPreferences.getProfilePic(ProfileActivity.this))
                    .apply(new RequestOptions().centerCrop())
                    .apply(new RequestOptions().placeholder(R.drawable.bikelogo))
                    .apply(new RequestOptions().error(R.drawable.bikelogo))
                    .into(new BitmapImageViewTarget(profileImage) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable =
                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    profileImage.setImageDrawable(circularBitmapDrawable);
                }
            });
        }


        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        10);

            }
        }
        if (ContextCompat.checkSelfPermission(ProfileActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ProfileActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(ProfileActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        10);
            }
        }

        if(AppPreferences.getIsactive(ProfileActivity.this)){
            switchActive.setChecked(true);
            switchActive.setText("Active");
        }else {
            switchActive.setChecked(false);
            switchActive.setText("In-Active");
        }

        switchActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                AppPreferences.setActive(ProfileActivity.this, b);
                if(b){
                    switchActive.setText("Active");
                }else {
                    switchActive.setText("In-Active");
                }
                driverStatus(b);
            }
        });

    }

    @OnClick(R.id.changepassword)
    public void onChangepasswordClicked() {
        password.setEnabled(true);
    }

    @OnClick(R.id.updateImage)
    public void onUpdateImageClicked() {
        selectImage();
    }

    @OnClick(R.id.update)
    public void onUpdateClicked() {
        String nameTxt = name.getText().toString();
        String mobileTxt = mobile.getText().toString();
        String emailTxt = email.getText().toString();
        String passwordTxt = password.getText().toString();
        // String driver_image = name.getText().toString();

        JSONObject object = new JSONObject();
        try {
            object.put("method", AppConstant.DRIVER_EDIT_PROFILE);
            object.put("driver_id", AppPreferences.getUserId(ProfileActivity.this));
            object.put("name", nameTxt);
            object.put("password", passwordTxt);
            object.put("email", emailTxt);
            object.put("contact_no", mobileTxt);
            object.put("driver_image", driver_image);
            updateProfile(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void selectImage() {


        final CharSequence[] items = {"Take Photo", "Choose from gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Select Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                // boolean result = UtilClass.checkPermission(MainActivity.this);
               if (items[item].equals("Take Photo")) {
                    // if (result)
                    cameraIntent();
                } else if (items[item].equals("Choose from gallery")) {
                    //  if (result)
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent() {
        startActivityForResult(createPickIntent(), REQUEST_LOAD_IMAGE);
    }

    @Nullable
    private Intent createPickIntent() {
        Intent picImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (picImageIntent.resolveActivity(getPackageManager()) != null) {
            return picImageIntent;
        } else {
            return null;
        }
    }


    private void cameraIntent() {

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/b4ebiker/";
        File newdir = new File(dir);
        newdir.mkdirs();

        String file = dir + "image.jpg";

        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();

        }
        //final Uri outputFileUri = Uri.fromFile(newfile);
        final Uri outputFileUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            outputFileUri = FileProvider.getUriForFile(ProfileActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 0) {
            File croppedImageFile1 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/b4ebiker/" + "image.jpg");
            final Uri outputFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                outputFileUri = FileProvider.getUriForFile(ProfileActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile1);
            } else {
                outputFileUri = Uri.fromFile(croppedImageFile1);
            }

            Logger.log("profileimage", outputFileUri.toString());

            Bitmap selectedImage = null;
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
                  selectedImage = Bitmap.createScaledBitmap(selectedImage, 200, 200, false);
                Logger.log("profileimage", selectedImage.getByteCount() + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
            profileImage.setImageBitmap(selectedImage);
            driver_image = encodeImage(selectedImage);


        }else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_LOAD_IMAGE && data != null) {

            Uri selectedImage = null;
            selectedImage = data.getData();
            //originalImageUri = data.getData();
            try {
                performCrop(selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CAMERA) {
            File croppedImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + "/b4ebiker/" + "image.jpg");

            final Uri originalFileUri;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                originalFileUri = FileProvider.getUriForFile(ProfileActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile);
            } else {

                originalFileUri = Uri.fromFile(croppedImageFile);
            }


            try {
                performCrop(originalFileUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PIC_CROP) {

            Bundle extras = data.getExtras();
            // get the cropped bitmap
            Bitmap selectedImage = extras.getParcelable("data");
            profileImage.setImageBitmap(selectedImage);
            driver_image = encodeImage(selectedImage);
        }
    }

    private void performCrop(Uri picUri) throws IOException {


        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(picUri, "image/*");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("aspectX", 4);
            cropIntent.putExtra("aspectY", 3);
            cropIntent.putExtra("outputX", 800);
            cropIntent.putExtra("outputY", 600);
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivityForResult(cropIntent, PIC_CROP);
        } catch (ActivityNotFoundException anfe) {
            anfe.printStackTrace();
        }

    }


    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void updateProfile(JSONObject jsonObject) {
        ProgressDialog.getInstance(ProfileActivity.this).show();
        RequestToServer.getInstance().send(jsonObject, AppConstant.B4E_DRIVER_BUSINESS_EDIT_PROFILE, new RequestToServer.CallBack() {
            @Override
            public void success(String json) {
                ProgressDialog.getInstance(ProfileActivity.this).dismiss();

                if (!json.equalsIgnoreCase("")) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("result");
                        JSONObject object = array.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            AppPreferences.setUsername(ProfileActivity.this, name.getText().toString());
                            AppPreferences.setPassword(ProfileActivity.this, password.getText().toString());
                            AppPreferences.setEmail(ProfileActivity.this, email.getText().toString());
                            AppPreferences.setProfilePic(ProfileActivity.this, object.optString("image"));
                            new AlertDialog.Builder(ProfileActivity.this)
                                    .setTitle("Success")
                                    .setMessage(object.getString("msg"))
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();

                                        }
                                    })
                                    .show();

                        } else {
                            String msg = object.getString("msg");
                            showAlertDialog(ProfileActivity.this, "Error!", msg, "Ok");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    showAlertDialog(ProfileActivity.this, "Error!", "Something went wrong please inform to admin", "Ok");
                }
            }
        });
    }

    public void driverStatus(boolean b){

        JSONObject object = new JSONObject();
        try {
            object.put("method", AppConstant.DRIVER_ONLINE_UPDATE);
            object.put("driver_id", AppPreferences.getUserId(ProfileActivity.this));
            object.put("isOnline", b ? 1 : 0);

            RequestToServer.getInstance().send(object, AppConstant.B4E_DRIVER_BUSINESS_UPDATE_LOGS, new RequestToServer.CallBack() {
                @Override
                public void success(String json) {

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
