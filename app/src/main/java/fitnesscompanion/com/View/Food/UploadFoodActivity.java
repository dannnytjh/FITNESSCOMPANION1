package fitnesscompanion.com.View.Food;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fitnesscompanion.com.R;
import fitnesscompanion.com.View.Home.MenuActivity;

public class UploadFoodActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageToUpload, downloadedImage;

    Button bUploadImage, bDownloadImage, bChooseImage;
    EditText NAME, etDownloadName;
    Bitmap bitmap;
    private String UploadUrl = "http://i2hub.tarc.edu.my:8886/FitnessCompanion/ImageUploadApp/updateinfo.php";
    private Uri mPhotoUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_food);

        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);

        bChooseImage = (Button) findViewById(R.id.bChooseImage);
        bUploadImage = (Button) findViewById(R.id.bUploadImage);

        NAME = (EditText) findViewById(R.id.name);


        bChooseImage.setOnClickListener(this);
        bUploadImage.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.bChooseImage:
                editImage();
                break;


            case R.id.bUploadImage:
                uploadImage();
                break;


        }

    }

//    private void selectImage()
//    {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, 1);
//
//    }


    private void editImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.addPhoto)).setItems(getResources().
                getStringArray(R.array.photoArray),new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int option) {
                if(option==0) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 0);
                    }

                }

                else if(option==1){
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI),1);
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0 && resultCode == RESULT_OK && data != null) {

            Bundle extras = data.getExtras();
            bitmap = (Bitmap) extras.get("data");
            imageToUpload.setImageBitmap(bitmap);
            imageToUpload.setVisibility(View.VISIBLE);
            NAME.setVisibility(View.VISIBLE);


        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                    imageToUpload.setImageBitmap(bitmap);
                    imageToUpload.setVisibility(View.VISIBLE);
                    NAME.setVisibility(View.VISIBLE);

                } catch (IOException e) {
                    e.printStackTrace();
                }


        }
    }

    private void uploadImage()
    {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UploadUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String Response = jsonObject.getString("response");
                            Toast.makeText(UploadFoodActivity.this,Response, Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })

        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("name", NAME.getText().toString().trim());
                params.put("image",imageToString(bitmap));

                return params;


            }
        };
        MySingleton.getInstance(UploadFoodActivity.this).addToRequestQue(stringRequest);
    }



        private String imageToString(Bitmap bitmap)
    {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(this,MenuActivity.class).putExtra("index",2));
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
