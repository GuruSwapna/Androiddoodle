package com.example.enchanterswapna.androiddoodle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class Maincam extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    ImageView mimageView,image2;
    Button button;
    private int requestCode;
    private int resultCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maincam);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mimageView = (ImageView) this.findViewById(R.id.image_from_camera);
        //image2=(ImageView)this.findViewById(R.id.image_from_camera1);
        button = (Button) this.findViewById(R.id.take_image_from_camera);


    }
    public void takeImageFromCamera(View view) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap mphoto = (Bitmap) data.getExtras().get("data");
//            Bitmap mphoto1=(Bitmap) data.getExtras().get("data1");
            mimageView.setImageBitmap(mphoto);
//            image2.setImageBitmap(mphoto1);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mphoto.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            mphoto1.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//
//            startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            byte[] byteArray = stream.toByteArray();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("picture", byteArray);
            startActivity(intent);
        }
    }

}
