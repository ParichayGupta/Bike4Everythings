package com.b4edriver.DriverApp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.b4edriver.AppController;
import com.b4edriver.BuildConfig;
import com.b4edriver.CommonClasses.ServerConnection.JSONParser;
import com.b4edriver.CommonClasses.ServerConnection.ServerErrorCallBack;
import com.b4edriver.CommonClasses.ServerConnection.Util;
import com.b4edriver.CommonClasses.ServerConnection.VolleyCallBack;
import com.b4edriver.Database.DBAdapter_Driver;
import com.b4edriver.Model.UserDriver;
import com.b4edriver.CommonClasses.Utils.AppConstantDriver;
import com.b4edriver.CommonClasses.Utils.AppPreferencesDriver;
import com.b4edriver.CommonClasses.Utils.DialogManagerDriver;
import com.b4edriver.CommonClasses.Utils.Function;
import com.b4edriver.R;
import com.b4elibrary.Logger;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


import eu.janmuller.android.simplecropimage.CropImage;

public class ProfileActivityDriver extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_GALLARY = 1;
    ProfileActivityDriver instance = null;
    ImageView userImage;
    EditText name_tv, address_tv, phone_tv, email_tv, password_tv, conf_password_tv;
    DialogManagerDriver dialogManager;
    Button btn_update;
    DBAdapter_Driver dbAdapter_user;
    String strUserImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_driver);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.leftarrow);
        instance = this;

        init();
        listener();

        dbAdapter_user = new DBAdapter_Driver(instance);

        dialogManager = new DialogManagerDriver();

        UserDriver user = dbAdapter_user.getUser();



        if (user != null) {
            name_tv.setText(user.getName());
            phone_tv.setText(user.getPhone());
            email_tv.setText(user.getEmailId());

            // email.setText(user.getEmailId());
            try {
                final String url = user.getUserImage();
                Logger.log("ImageUrl", url);
                ImageLoader imageLoader = AppController.getInstance(ProfileActivityDriver.this).getImageLoader();
                imageLoader.get(url, ImageLoader.getImageListener(userImage, android.R.drawable.ic_dialog_alert, R.drawable.user));

            } catch (NullPointerException e) {

            }
        }

        if (Build.VERSION.SDK_INT >= 23) {
            boolean b = Function.hasPermissionInManifest(instance, "android.permission.CAMERA");
            Logger.log("hasPermissionInManifest", b + "");
        }
        int permissionCheck = ContextCompat.checkSelfPermission(instance,
                Manifest.permission.CAMERA);

        if (ContextCompat.checkSelfPermission(instance,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(instance,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    10);
            // Should we show an explanation?
            /*if (ActivityCompat.shouldShowRequestPermissionRationale(instance,
                    Manifest.permission.CAMERA)) {

            } else {

                ActivityCompat.requestPermissions(instance,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        10);
            }*/
        }

    }

    private void init() {
        userImage = (ImageView) findViewById(R.id.userImage);
        name_tv = (EditText) findViewById(R.id.name_tv);
        //  address_tv = (EditText) findViewById(R.id.address_tv);
        phone_tv = (EditText) findViewById(R.id.phone_tv);
        email_tv = (EditText) findViewById(R.id.email_tv);
        btn_update = (Button) findViewById(R.id.btn_update);
        password_tv = (EditText) findViewById(R.id.password_tv);
        conf_password_tv = (EditText) findViewById(R.id.conf_password_tv);
    }

    private void listener() {
        userImage.setOnClickListener(this);
        btn_update.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.btn_update) {
            String name = name_tv.getText().toString();
            String password = password_tv.getText().toString();
            String conf_pass = conf_password_tv.getText().toString();

            /*if (TextUtils.isEmpty(password)) {
                password_tv.setError("Please enter your password");
            } else */
            if (!password.equalsIgnoreCase(conf_pass)) {
                Toast.makeText(instance, "Password not match", Toast.LENGTH_LONG).show();
            } else {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("method", AppConstantDriver.METHOD.UPDATEPROFILE);
                    jsonObject.put("id", AppPreferencesDriver.getDriverId(instance));
                    jsonObject.put("name", name);
                    jsonObject.put("password", password);
                    jsonObject.put("image", strUserImage);


                    UpdateProfileTask(jsonObject);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else if (i == R.id.userImage) {
            selectImage();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.left_to_right,
                        R.anim.right_to_left);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void selectImage() {
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/bike4everything/";
        File newdir = new File(dir);
        newdir.mkdirs();

        String file = dir + "profile.jpg";
        Logger.log("imagesss cam11", file);
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }

        final Uri outputFileUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            outputFileUri = FileProvider.getUriForFile(instance,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        final CharSequence[] items = {"Take Photo", "Choose from Gallary", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(instance);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent;
                    intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Gallary")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            REQUEST_GALLARY);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }

            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File croppedImageFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/bike4everything/" + "profile.jpg");

        if ((requestCode == REQUEST_GALLARY) && (resultCode == RESULT_OK)) {


            Uri photoUri = data.getData();

            String filePath = "";
            if (photoUri != null){
                try {
                    //We get the file path from the media info returned by the content resolver
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(photoUri, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    filePath = cursor.getString(columnIndex);

                    cursor.close();
                    croppedImageFile = new File(filePath);

                    Intent intent = new Intent(this, CropImage.class);
                    String filePath1 = croppedImageFile.getAbsolutePath();
                    intent.putExtra(CropImage.IMAGE_PATH, filePath1);
                    intent.putExtra(CropImage.SCALE, true);
                    intent.putExtra(CropImage.ASPECT_X, 2);
                    intent.putExtra(CropImage.ASPECT_Y, 2);

                    startActivityForResult(intent, 2);


                }catch(Exception e){
                }

            }

        } else if ((requestCode == REQUEST_CAMERA) && (resultCode == RESULT_OK)) {

            Intent intent = new Intent(this, CropImage.class);
            String filePath = croppedImageFile.getAbsolutePath();
            intent.putExtra(CropImage.IMAGE_PATH, filePath);
            intent.putExtra(CropImage.SCALE, true);
            intent.putExtra(CropImage.ASPECT_X, 2);
            intent.putExtra(CropImage.ASPECT_Y, 2);

            startActivityForResult(intent, 2);




        } else if ((requestCode == 2) && (resultCode == RESULT_OK)) {

            String path = data.getStringExtra(CropImage.IMAGE_PATH);
            if (path == null) {
                return;
            }
            Bitmap thePic = BitmapFactory.decodeFile(path);
            userImage.setImageBitmap(thePic);
            strUserImage = Function.encodeImg1Tobase64(thePic);


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }
        }
    }

    public void UpdateProfileTask(JSONObject jsonObject){
        dialogManager.showProcessDialog(instance, "Update your profile",false);
        JSONParser jsonParser = new JSONParser(instance);
        jsonParser.parseVollyObject(AppConstantDriver.URL.ONDEMAND_DRIVER_DRIVERPROFILE, 1, jsonObject,  new VolleyCallBack() {
            @Override
            public void success(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equalsIgnoreCase("200")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject object = jsonArray.getJSONObject(0);
                        UserDriver user = new UserDriver();
                        user.setName(object.getString("name"));
                        user.setUserImage(object.getString("image"));
                        user.setId(Long.valueOf(AppPreferencesDriver.getDriverId(instance)));
                        dbAdapter_user.updateDriver(user);

                        Intent intent11 = new Intent(instance, NavigationDrawerDriver.class);
                        intent11.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent11.setAction("");
                        startActivity(intent11);
                        overridePendingTransition(R.anim.left_to_right,
                                R.anim.right_to_left);
                        finish();
                    }else if(jsonObject.getString("status").equalsIgnoreCase("900")){
                        Intent intent = new Intent(instance, DialogActivityDriver.class);
                        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setAction("loginSessionExpire");
                        intent.putExtra("msg",jsonObject.getString("message"));
                        startActivity(intent);
                    }

                    dialogManager.stopProcessDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ServerErrorCallBack() {
            @Override
            public void error(String response) {
                try {
                    dialogManager.stopProcessDialog();
                    JSONObject jsonObject = new JSONObject(response);
                    new SweetAlertDialog(instance, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText(jsonObject.getString("vollymsg"))
                            .setConfirmText("Try again!")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.left_to_right,
                R.anim.right_to_left);
    }
}
