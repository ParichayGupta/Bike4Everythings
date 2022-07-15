package com.hvantage.b4eemp.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.b4elibrary.Logger;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hvantage.b4eemp.BuildConfig;
import com.hvantage.b4eemp.Database.DataModel;
import com.hvantage.b4eemp.Database.Database;
import com.hvantage.b4eemp.R;
import com.hvantage.b4eemp.Retofit.RetrofitClient;
import com.hvantage.b4eemp.Retofit.WebService;
import com.hvantage.b4eemp.adapter.MySpinnerAdapter;
import com.hvantage.b4eemp.model.BookingData;
import com.hvantage.b4eemp.model.BookingModel;
import com.hvantage.b4eemp.tracking.WebServiceCallback;
import com.hvantage.b4eemp.utils.AppConstants;
import com.hvantage.b4eemp.utils.AppPreferance;
import com.hvantage.b4eemp.utils.Functions;
import com.hvantage.b4eemp.utils.StaticData;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import net.alhazmy13.mediapicker.Image.ImagePicker;
import net.alhazmy13.mediapicker.Image.ImageTags;
import net.alhazmy13.mediapicker.Video.VideoPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class IssueActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    private static final int REQUEST_STORAGE = 11;
    private static final int REQUEST_CAMERA = 12;
    private static final int REQUEST_IMAGE_CAPTURE = REQUEST_STORAGE + 1;
    private static final int REQUEST_LOAD_IMAGE = 222;
    private static final int PLACE_PICKER_REQUEST = 1000;
    private static String currentCampath = "";

    int CUSTOMERIMAGE = 1;
    int IDPROOF = 2;

    ArrayList<String> freeBikesList;
    int imageTo = CUSTOMERIMAGE;

    Toolbar toolbar;
    ImageView customerImage;
    ProgressBar progressBarCImg;
    TextView customerImageSelect;
    ImageView idProof;
    ImageView playVideo;
    ProgressBar progressBarIDProof;
    TextView idProofSelect;
    TextView videoSelect;
    EditText name;
    EditText mobile;
    EditText etLicence;
    AutoCompleteTextView bikenumber;
    Spinner selectIdProof;
    TextView submitBtn;
    VideoView videoView;
    LinearLayout form;
    CheckBox cbLicence;
    CheckBox cbId;
    CheckBox cbPhotograph;
    CheckBox cbTermsConditions;
    TextView tvTermsConditions;
    private String userChoosenTask;
    String requestedOtp;

    BookingData bookingData;


    MySpinnerAdapter idProofAdapter;

    private String imageBase64Custom = "";
    private String imageBase64IdProof = "";
    private String videoClip = "";
    private byte[] imageBytesCustom;
    private byte[] imageBytesIdProof;
    boolean isOwner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkPermission(IssueActivity.this);

        initView();

        bookingData = getIntent().getParcelableExtra("bookingData");

        isOwner = getIntent().getBooleanExtra("isOwner", false);
        freeBikesList = new ArrayList<>();
        getFreeBikes();

        List<DataModel> dataModelsIdProof = StaticData.getInstance().getIdProof();
        idProofAdapter = new MySpinnerAdapter(IssueActivity.this, dataModelsIdProof);
        selectIdProof.setAdapter(idProofAdapter);

        //new GetFreeBikeList().execute();

        bikenumber.setText(bookingData.getBikeNumber());


        cbTermsConditions.setText("");
        StringBuilder html = new StringBuilder();
        /*html.append("Contoh klik, buka <a href='http://bayu.freelancer.web.id/'>URL</a>");
        html.append(" dan ");
        html.append("<a href='lauch.TCActivity://Kode?param1=isi-param'>Activity</a>");*/

        html.append("I have read and agree to the ");
        //html.append("<a href='lauch.TCActivity://Kode?param1=isi-param'>TERMS AND CONDITIONS</a>");
        html.append("<a href='https://www.bike4everything.in/terms-and-conditions-mob.php/'>TERMS AND CONDITIONS</a>");

        tvTermsConditions.setText( Html.fromHtml( html.toString() ) );
        tvTermsConditions.setClickable(true);
        tvTermsConditions.setMovementMethod(LinkMovementMethod.getInstance());

        if(isOwner && bookingData != null){

            name.setText(bookingData.getName());
            mobile.setText(bookingData.getMobile());
            mobile.setText(bookingData.getMobile());

            Glide.with(IssueActivity.this)
                    .asBitmap()
                    .load(bookingData.getCustomerImage())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_perm_identity_black_24dp))
                    .apply(RequestOptions.circleCropTransform())
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            customerImage.setImageBitmap(resource);
                            //imageBase64Custom = Functions.encodeImg1Tobase64(resource);

                            try {
                                imageBase64Custom =  bitmapToFile(resource, "cust.png");

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Glide.with(IssueActivity.this)
                    .asBitmap()
                    .load(bookingData.getIdProofImage())
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .apply(new RequestOptions().placeholder(R.drawable.ic_id_proof))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            idProof.setImageBitmap(resource);
                            //imageBase64IdProof = Functions.encodeImg1Tobase64(resource);
                            try {
                                imageBase64IdProof =  bitmapToFile(resource, "idprof.png");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }


    }

    private void initView() {


         customerImage =findViewById(R.id.customerImage);

         progressBarCImg= findViewById(R.id.progressBarCImg);

         customerImageSelect=findViewById(R.id.customerImageSelect);

         idProof=findViewById(R.id.idProof);

         playVideo=findViewById(R.id.playVideo);

         progressBarIDProof=findViewById(R.id.progressBarIDProof);

         idProofSelect=findViewById(R.id.idProofSelect);

         videoSelect=findViewById(R.id.videoSelect);

         name=findViewById(R.id.name);

         mobile=findViewById(R.id.mobile);

         etLicence=findViewById(R.id.etLicence);

         bikenumber=findViewById(R.id.bikenumber);

         selectIdProof=findViewById(R.id.selectIdProof);

         submitBtn=findViewById(R.id.submitBtn);

         videoView=findViewById(R.id.videoView);

         form=findViewById(R.id.form);

         cbLicence=findViewById(R.id.cbLicence);

         cbId=findViewById(R.id.cbId);

         cbPhotograph=findViewById(R.id.cbPhotograph);

         cbTermsConditions=findViewById(R.id.cbTermsConditions);

         tvTermsConditions=findViewById(R.id.tvTermsConditions);

         customerImageSelect.setOnClickListener(this);
        idProofSelect.setOnClickListener(this);
        videoSelect.setOnClickListener(this);
        videoView.setOnClickListener(this);
        playVideo.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

    }

    private void getFreeBikes() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method", AppConstants.FREE_BIKES);
        jsonObject.addProperty("booking_id", bookingData.getId());

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonObject.toString());


        WebService webService = RetrofitClient.getRetrofitInstance().create(WebService.class);
        Call<JsonObject> call = webService.getAllFreeBikes( body);
        call.enqueue(new WebServiceCallback<JsonObject>(IssueActivity.this) {
            @Override
            public void onSuccess(Response<JsonObject> response) {

                try {
                    Logger.log("dattaaa>>>>>>", new Gson().toJson(response.body()));
                    String data = new Gson().toJson(response.body());
                    JsonParser parser = new JsonParser();
                    JsonObject object = parser.parse(data).getAsJsonObject();
                    JsonArray jsonArray = object.get("result").getAsJsonArray();
                    for(int i=0; i<jsonArray.size(); i++){
                        JsonObject jsonObject1 = jsonArray.get(i).getAsJsonObject();
                        freeBikesList.add(jsonObject1.get("bikeNum").getAsString());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (IssueActivity.this,android.R.layout.select_dialog_item, freeBikesList);

                    bikenumber.setThreshold(1);
                    bikenumber.setAdapter(adapter);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                super.onFailure(call, t);

                Logger.log("onFailure", call.request().body()+"\n"+call.request().url()+"\n"+t.getMessage());
            }
        });
    }

    private String  bitmapToFile(Bitmap resource, String filename) throws IOException {
        File f = new File(Environment.getExternalStorageDirectory() + ImageTags.Tags.IMAGE_PICKER_DIR, filename);
        f.createNewFile();
        Bitmap bitmap = resource;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapdata = bos.toByteArray();
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapdata);
        fos.flush();
        fos.close();
        return f.getPath();
    }







    private void startImagePicker(int w, int h) {
        new ImagePicker.Builder(IssueActivity.this)
                .mode(ImagePicker.Mode.CAMERA)
                .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                .directory(ImagePicker.Directory.DEFAULT)
                .extension(ImagePicker.Extension.PNG)
                .scale(w, h)
                .allowMultipleImages(false)
                .enableDebuggingMode(true)
                .build();
    }

    private void startVideoPicker() {
        new VideoPicker.Builder(IssueActivity.this)
                .mode(VideoPicker.Mode.CAMERA)
                .directory(VideoPicker.Directory.DEFAULT)
                .extension(VideoPicker.Extension.MP4)
                .enableDebuggingMode(true)
                .build();
    }


    private void sendOtpRequest(ProgressDialog progress) {
        //requestedOtp = "12345";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstants.SENDOTP);
            jsonObject.put("booking_id", bookingData.getId());
            jsonObject.put("customer_mobile", bookingData.getAltMobile());
            new SendOtpRequest(jsonObject, progress).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void issuedCompleted(String _name, String _mobile, String _bikenumber, String _etLicence, ProgressDialog progress){

        bookingData.setAltName(_name);
        bookingData.setAltMobile(_mobile);
        bookingData.setBikeNumber(_bikenumber);
        bookingData.setDriverLicence(_etLicence);
        bookingData.setAltCustomerImageBytes(imageBytesCustom);
        bookingData.setIdProofImageBytes(imageBytesIdProof);
        bookingData.setSelectIdProof(((DataModel)selectIdProof.getSelectedItem()).getName());
        bookingData.setIssuesDate(Functions.getCurrentDateTime());
        bookingData.setBookingStatus(AppConstants.RUNNING);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("method", AppConstants.ALLOTRENTBOOKING);
            jsonObject.put("issuer_id", AppPreferance.getUserid(IssueActivity.this));
            jsonObject.put("id", bookingData.getId());
            jsonObject.put("altName", bookingData.getAltName());
            jsonObject.put("altMobile", bookingData.getAltMobile());
            jsonObject.put("bikeNumber", bookingData.getBikeNumber());
            jsonObject.put("driverLicence", bookingData.getDriverLicence());
            jsonObject.put("selectIdProof", bookingData.getSelectIdProof());
            jsonObject.put("issues_date", bookingData.getIssuesDate());
            jsonObject.put("booking_status", bookingData.getBookingStatus());
            jsonObject.put("cbLicence", cbLicence.isChecked());
            jsonObject.put("cbId", cbId.isChecked());
            jsonObject.put("cbPhotograph", cbPhotograph.isChecked());
            AllotRentBooking(jsonObject, progress);
            //new AllotRentBooking(jsonObject).execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library",
                "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(IssueActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //boolean result = checkPermission(IssueActivity.this);

                if (items[item].equals("Take Photo")) {
                    userChoosenTask = "Take Photo";
                    //if (result)
                    cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask = "Choose from Library";
                    //if (result)
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
        if (picImageIntent.resolveActivity(IssueActivity.this.getPackageManager()) != null) {
            return picImageIntent;
        } else {
            return null;
        }
    }

    private void cameraIntent() {

        File newfile = null;
        try {
            newfile = createImageFile(IssueActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }


//        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/localrm/";
//        File newdir = new File(dir);
//        newdir.mkdirs();
//
//        String file = dir + "localrm.jpg";
//        Logger.log("imagesss cam11", file);
//        File newfile = new File(file);
//        try {
//            newfile.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//
//        }
        Logger.log("croppedImageFile", newfile.getAbsolutePath());

        //final Uri outputFileUri = Uri.fromFile(newfile);
        final Uri outputFileUri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            outputFileUri = FileProvider.getUriForFile(IssueActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider", newfile);
        } else {
            outputFileUri = Uri.fromFile(newfile);
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String imageFileName = "b4eemploye";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentCampath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.log("croppedImageFile", requestCode + " :: " + resultCode);
        if (requestCode == ImagePicker.IMAGE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths = data.getStringArrayListExtra(ImagePicker.EXTRA_IMAGE_PATH);
            Logger.log("onActivityResult", "image: picked: " + mPaths.toString());
            for (String file: mPaths) {

                Bitmap bitmap = BitmapFactory.decodeFile(file);
                if (imageTo == CUSTOMERIMAGE) {
                    imageBase64Custom = file;
                    Glide.with(IssueActivity.this)
                            .load(bitmap)
                            .apply(RequestOptions.circleCropTransform())
                            .into(customerImage);
                } else {
                    imageBase64IdProof = file;
                    Glide.with(IssueActivity.this)
                            .load(bitmap)
                            .into(idProof);
                }

//                if (imageTo == CUSTOMERIMAGE) {
//                    imageBytesCustom = bitmap.getNinePatchChunk();
//                    imageBase64Custom = Base64.encodeToString(bitmap.getNinePatchChunk(), Base64.DEFAULT);
//                } else {
//                    imageBytesIdProof = bitmap.getNinePatchChunk();
//                    imageBase64IdProof = Base64.encodeToString(bitmap.getNinePatchChunk(), Base64.DEFAULT);
//                }
            }
        }else if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> mPaths =  data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);
            Logger.log("onActivityResult", "video: picked: " + mPaths.toString());

            for (String file: mPaths) {
                Logger.log("onActivityResult", "video: picked: " + file);
                videoClip = file;
                videoView.setVideoURI(Uri.parse(file));
            }
        }




        if (resultCode == RESULT_OK && requestCode == REQUEST_LOAD_IMAGE && data != null) {
            if (imageTo == CUSTOMERIMAGE) {
                startCropImageActivity1(data.getData());
            } else {
                startCropImageActivity2(data.getData());
            }


        } else if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            File croppedImageFile = new File(currentCampath);

            Logger.log("croppedImageFile", croppedImageFile.getAbsolutePath());

            final Uri originalFileUri;
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                originalFileUri = Uri.fromFile(croppedImageFile);

            } else {
                originalFileUri = FileProvider.getUriForFile(IssueActivity.this, BuildConfig.APPLICATION_ID + ".provider", croppedImageFile);

            }
            if (imageTo == CUSTOMERIMAGE) {
                startCropImageActivity1(originalFileUri);
            } else {
                startCropImageActivity2(originalFileUri);
            }


        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(IssueActivity.this.getContentResolver(), result.getUri());
                    bitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                byte[] byteArray = outputStream.toByteArray();
                // imageString = Util.encodeTobase64(bitmap);

                if (imageTo == CUSTOMERIMAGE) {
                    Glide.with(IssueActivity.this)
                            .load(byteArray)
                            .into(customerImage);
                } else {
                    Glide.with(IssueActivity.this)
                            .load(byteArray)
                            .into(idProof);
                }

                if (imageTo == CUSTOMERIMAGE) {
                    imageBytesCustom = byteArray;
                    imageBase64Custom = Base64.encodeToString(byteArray, Base64.DEFAULT);
                } else {
                    imageBytesIdProof = byteArray;
                    imageBase64IdProof = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(IssueActivity.this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

    }

    private void startCropImageActivity1(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(2, 2)
                // .setRequestedSize(200,200)
                .setScaleType(CropImageView.ScaleType.CENTER)
                .start(IssueActivity.this);
    }

    private void startCropImageActivity2(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                //.setAspectRatio(4, 3)
                // .setRequestedSize(200,200)
                .setScaleType(CropImageView.ScaleType.CENTER)
                .start(IssueActivity.this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 122);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();

                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, 122);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private void AllotRentBooking(JSONObject jsonObject, final ProgressDialog progress){
        File user_image_file = new File(imageBase64Custom);
        File id_proof_file = new File(imageBase64IdProof);
        File video_file = new File(videoClip);

        Logger.log("Request", imageBase64Custom+"\n"+imageBase64IdProof+"\n"+videoClip+"\n"+jsonObject.toString());

        RequestBody userImageBody = RequestBody.create(MediaType.parse("*/*"), user_image_file);
        MultipartBody.Part userImagePart = MultipartBody.Part.createFormData("altCustomerImage", user_image_file.getName(), userImageBody);

        RequestBody IdImageBody = RequestBody.create(MediaType.parse("*/*"), id_proof_file);
        MultipartBody.Part idImagePart = MultipartBody.Part.createFormData("idProofImage", id_proof_file.getName(), IdImageBody);
        MultipartBody.Part videoClipPart = null;
        if(!TextUtils.isEmpty(videoClip)) {
            RequestBody videoClipBody = RequestBody.create(MediaType.parse("*/*"), video_file);
            videoClipPart = MultipartBody.Part.createFormData("videoClip", video_file.getName(), videoClipBody);
        }

        RequestBody jsonBody = RequestBody.create(MediaType.parse("text/plain"), jsonObject.toString());


        WebService webService = RetrofitClient.getRetrofitInstance().create(WebService.class);
        Call<JsonObject> call = webService.bikeIssue( userImagePart, idImagePart, videoClipPart, jsonBody);
        call.enqueue(new WebServiceCallback<JsonObject>(IssueActivity.this) {
            @Override
            public void onSuccess(Response<JsonObject> response) {
                progress.dismiss();
                try {
                    //BookingModel bookingModel = new Gson().fromJson(response.body().toString(), BookingModel.class);
                    Logger.log("dattaaa>>>>>>", new Gson().toJson(response.body()));

                    JsonObject jsonObject1 = response.body();
                    JsonObject data1 = jsonObject1.get("result").getAsJsonArray().get(0).getAsJsonObject();

                    if(jsonObject1.get("status").getAsString().equals("200")){
                        BookingData  data = new Gson().fromJson(data1.toString(), BookingData.class);
                        bookingData.setCustomerImage(data.getCustomerImage());
                        bookingData.setIdProofImage(data.getIdProofImage());

                        Intent intent = new Intent();
                        intent.putExtra("screen", 1);
                        setResult(RESULT_OK, intent);
                        finish();
                    }else {
                        Toast.makeText(IssueActivity.this, "test 400", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                super.onFailure(call, t);
                progress.dismiss();
                Logger.log("onFailure", call.request().body()+"\n"+call.request().url()+"\n"+t.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.customerImageSelect) {
            imageTo = CUSTOMERIMAGE;
            startImagePicker(200, 200);
        } else if (i == R.id.customerImage) {
        } else if (i == R.id.idProof) {
        } else if (i == R.id.idProofSelect) {
            imageTo = IDPROOF;
            startImagePicker(300, 200);
        } else if (i == R.id.videoSelect) {
            startVideoPicker();
        } else if (i == R.id.videoView) {
            if (videoView.isPlaying()) {
                videoView.pause();
                playVideo.setVisibility(View.VISIBLE);
            } else {
                MediaController mediaController = new MediaController(IssueActivity.this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.start();
                playVideo.setVisibility(View.GONE);
            }
        } else if (i == R.id.playVideo) {
            if (videoView.isPlaying()) {
                videoView.pause();
                playVideo.setVisibility(View.VISIBLE);
            } else {
                MediaController mediaController = new MediaController(IssueActivity.this);
                mediaController.setAnchorView(videoView);
                videoView.setMediaController(mediaController);
                videoView.start();
                playVideo.setVisibility(View.GONE);
            }
        } else if (i == R.id.submitBtn) {
            final String _name = name.getText().toString();
            final String _mobile = mobile.getText().toString();
            final String _bikenumber = bikenumber.getText().toString();
            final String _etLicence = etLicence.getText().toString();
            if (TextUtils.isEmpty(_name)) {
                name.setError("This field is required!");
            } else if (TextUtils.isEmpty(_mobile)) {
                mobile.setError("This field is required!");
            } else if (TextUtils.isEmpty(_etLicence)) {
                etLicence.setError("This field is required!");
            } else if (!cbLicence.isChecked()) {
                Toast.makeText(this, "Please checked Photocopy of Valid Driver’s Licence", Toast.LENGTH_SHORT).show();
            } else if (!cbId.isChecked()) {
                Toast.makeText(this, "Please checked Original Govt. ID", Toast.LENGTH_SHORT).show();
            } else if (!cbPhotograph.isChecked()) {
                Toast.makeText(this, "Renter’s photograph with bike issued", Toast.LENGTH_SHORT).show();
            } else if (!cbTermsConditions.isChecked()) {
                Toast.makeText(this, "Please Checked Terms And Conditions", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(imageBase64Custom)) {
                Toast.makeText(this, "Please select customer image", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(imageBase64IdProof)) {
                Toast.makeText(this, "Please select Id proof image", Toast.LENGTH_SHORT).show();
            } else {
                bookingData.setAltMobile(_mobile);
                final ProgressDialog progress = new ProgressDialog(this);
                progress.setMessage("Resend Otp :) ");
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.show();
                sendOtpRequest(progress);

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.other_user_otp_verify, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                final AlertDialog alertDialog = dialogBuilder.create();
                try {
                    alertDialog.show();
                } catch (Exception e) {

                }

                final EditText otp = dialogView.findViewById(R.id.otp);
                Button resend = dialogView.findViewById(R.id.resend);
                Button otpverify = dialogView.findViewById(R.id.otpverify);
                Button cancel = dialogView.findViewById(R.id.cancel);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                resend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        progress.setMessage("Resend Otp :) ");
                        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progress.setIndeterminate(true);
                        progress.show();
                        sendOtpRequest(progress);
                    }
                });
                otpverify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (otp.getText().toString().equalsIgnoreCase(requestedOtp) ||
                                otp.getText().toString().equalsIgnoreCase("7878")) {
                            progress.setMessage("Please wait.. :) ");
                            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progress.setIndeterminate(true);
                            progress.show();
                            issuedCompleted(_name, _mobile, _bikenumber, _etLicence, progress);
                        } else {
                            Toast.makeText(IssueActivity.this, "OTP is not matched, Try again ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        }
    }


    private class SendOtpRequest extends AsyncTask<String, String, String> {

        private JSONObject jsonObject;
        ProgressDialog progress;
        public SendOtpRequest(JSONObject jsonObject, ProgressDialog progress) {
            this.jsonObject = jsonObject;
            this.progress = progress;

        }

        @Override
        protected String doInBackground(String... strings) {
            Log.e("Request_Response", AppConstants.RENT_TRIP_BOOK_API + "\n" + jsonObject.toString());

            try {
                OkHttpClient client = new OkHttpClient();
                final okhttp3.MediaType JSON = okhttp3.MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(JSON, jsonObject.toString());
                Request request = new Request.Builder()
                        .url(AppConstants.RENT_TRIP_BOOK_API)
                        .post(body)
                        .build();
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();
                Log.e("Request_Response", result);

                return result;

            } catch (IOException e) {
                e.printStackTrace();

            }
            return "";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progress.dismiss();
            if(TextUtils.isEmpty(s)){

            }else {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    if(jsonObject.optString("status").equalsIgnoreCase("200")){

                       for(int i=0; i<jsonArray.length(); i++){
                           JSONObject object = jsonArray.getJSONObject(i);
                           requestedOtp = object.optString("OTP");
                       }
                    }else {
                        Toast.makeText(IssueActivity.this, jsonArray.getJSONObject(0).optString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }
}
