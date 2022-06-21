package com.bike4everythingbussiness.Activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bike4everythingbussiness.BuildConfig;
import com.bike4everythingbussiness.Model.User;
import com.bike4everythingbussiness.R;
import com.bike4everythingbussiness.Services.RequestToServer;
import com.bike4everythingbussiness.Utils.AppConstant;
import com.bike4everythingbussiness.Utils.CircleImageView;
import com.bike4everythingbussiness.Utils.Logger;
import com.bumptech.glide.Glide;
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
    String driver_image_path = "";
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

        name.setText(currentUser.getName());
        mobile.setText(currentUser.getPhone_number());
        email.setText(currentUser.getEmail());
        password.setText(currentUser.getPassword());
        String image = currentUser.getProfile_image();
        Logger.log("profileimage", image + " >>>>");
        if (!TextUtils.isEmpty(image)) {
            Glide.with(getApplicationContext())
                    .load(image.contains("http") ? image : Uri.parse(image))
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.bikelogo)
                    .error(R.drawable.bikelogo)
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
            object.put("method", AppConstant.BUSINESS_EDIT_PROFILE);
            object.put("business_id", currentUser.getId());
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
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_LOAD_IMAGE && data != null) {

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
        } else if ( resultCode == RESULT_OK && requestCode == PIC_CROP) {

            Bundle extras = data.getExtras();
            // get the cropped bitmap
            Bitmap selectedImage = extras.getParcelable("data");
            profileImage.setImageBitmap(selectedImage);
            driver_image = encodeImage(selectedImage);
            driver_image_path = getImagePath(ProfileActivity.this, selectedImage);

        }
    }

    public String getImagePath(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Profile", null);
        return path;
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

        RequestToServer.getInstance().send(ProfileActivity.this,jsonObject, AppConstant.B4E_BUSINESS_EDIT_PROFILE, new RequestToServer.CallBack() {
            @Override
            public void success(String json) {

                if (!json.equalsIgnoreCase("")) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(json);
                        JSONArray array = jsonObject.getJSONArray("result");
                        JSONObject object = array.getJSONObject(0);
                        if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                            User contact = new User();
                            contact.setId(currentUser.getId());
                            contact.setName(name.getText().toString());
                            contact.setPhone_number(mobile.getText().toString());
                            contact.setProfile_image(object.optString("image"));
                            contact.setEmail(email.getText().toString());
                            contact.setPassword(password.getText().toString());

                            databaseHandler.updateContact(contact);

                            Intent intent = new Intent("profileUpdate");
                            LocalBroadcastManager.getInstance(ProfileActivity.this).sendBroadcast(intent);

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


}
