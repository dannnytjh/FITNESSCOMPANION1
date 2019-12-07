package fitnesscompanion.com.View.Food;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fitnesscompanion.com.R;
import fitnesscompanion.com.Util.DatePicker;
import fitnesscompanion.com.View.Home.MenuActivity;
import fitnesscompanion.com.View.Profile.ReminderActivity;

public class UploadFoodActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageToUpload;
    RelativeLayout bChooseImage, bList;
    EditText name, price;
    Button bUploadImage;
    Bitmap bitmap;
    int traineeId;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    public static SQLiteHelper sqLiteHelper;

    private String UploadUrl = "http://i2hub.tarc.edu.my:8886/FitnessCompanion/ImageUploadApp/updateinfo.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_food);
        traineeId = this.getIntent().getIntExtra("id",0);
        imageToUpload = (ImageView) findViewById(R.id.imageToUpload);

        bChooseImage = (RelativeLayout) findViewById(R.id.bChooseImage);
        bUploadImage = (Button) findViewById(R.id.bUploadImage);
        bList = (RelativeLayout) findViewById(R.id.bList);

        name = (EditText) findViewById(R.id.name);
        price = (EditText) findViewById(R.id.price);

        bUploadImage.setOnClickListener(this);
        bChooseImage.setOnClickListener(this);
        bList.setOnClickListener(this);

        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        mDisplayDate.setOnClickListener(this);
        

        sqLiteHelper = new SQLiteHelper(this, "FoodDB.sqlite", null, 1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS FOOD (Id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR (200), price VARCHAR (200), image BLOB)");


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.bChooseImage:
                editImage();
                break;


            case R.id.bUploadImage:
                uploadImage();
                uploadImageSQL();
                break;

            case R.id.bList:
                Intent intent = new Intent (UploadFoodActivity.this, FoodList.class);
                startActivity(intent);
                break;

            case R.id.tvDate:
                Calendar calender = Calendar.getInstance();
                int year = calender.get(Calendar.YEAR);
                int month = calender.get(Calendar.MONTH);
                int day = calender.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        UploadFoodActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
        }

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int year, int month, int day) {
                month = month + 1;
                Log.d("OnDateSet", "OnDateSet: dd/mm/yyyy: " + day + "/" + month + "/" + year);
                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };



    }

    private void editImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.addPhoto)).setItems(getResources().
                getStringArray(R.array.photoArray), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int option) {
                if (option == 0) {

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 0);
                    }

                } else if (option == 1) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), 1);
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


        } else if (requestCode == 1 && resultCode == RESULT_OK && data != null) {

            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageToUpload.setImageBitmap(bitmap);
                imageToUpload.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    //upload to SQLite
    private void uploadImageSQL() {
        try {
            sqLiteHelper.insertData(
                    name.getText().toString().trim(),
                    price.getText().toString().trim(),
                    imageViewToByte(imageToUpload)

            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    //upload to server
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
                            imageToUpload.setImageResource(0);
                            imageToUpload.setVisibility(View.GONE);
                            name.getText().clear();

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
                params.put("name", name.getText().toString().trim());
                params.put("image",imageToString(bitmap));
                params.put("id",String.valueOf(traineeId));

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

    //return to food fragment
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

