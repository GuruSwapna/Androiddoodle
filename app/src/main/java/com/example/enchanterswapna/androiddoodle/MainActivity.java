package com.example.enchanterswapna.androiddoodle;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private DrawingDoodle drawDoodle;
    private ColorPicker colorPicker;
    private ImageButton currPaintColor;
    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 23) {
            checkAndRequestPermissions();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        colorPicker = new ColorPicker(MainActivity.this, 0, 0, 0, 0);
        drawDoodle = (DrawingDoodle)findViewById(R.id.drawing);


        LinearLayout paletteLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaintColor = (ImageButton)paletteLayout.getChildAt(0);
        currPaintColor.setImageResource(R.drawable.paint_selected);

        ImageButton newCanvas = (ImageButton)findViewById(R.id.new_btn);
        ImageButton changeCanvasBgColor = (ImageButton)findViewById(R.id.draw_btn);
        ImageButton clearCanvas = (ImageButton)findViewById(R.id.erase_btn);
        ImageButton saveCanvas = (ImageButton)findViewById(R.id.save_btn);

//        Intent intent=getIntent();
//        ImageView iv_photo=(ImageView)findViewById(R.id.drawing);
//        Bundle extras = getIntent().getExtras();
//        byte[] byteArray = extras.getByteArray("picture");
//
//        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//
//        iv_photo.setImageBitmap(bmp);
    }

    // Save Doodle to Gallery
    public void saveCanvasToGallery(View view){
        drawDoodle.setDrawingCacheEnabled(true);
        String imgSaved = MediaStore.Images.Media.insertImage(
                getContentResolver(), drawDoodle.getDrawingCache(),
                UUID.randomUUID().toString(),"DroidDoodle");

        if(imgSaved!=null){
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "Doodle saved to Gallery!", Toast.LENGTH_SHORT);
            savedToast.show();
        }
        else{
            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                    "Oops! Doodle could not be saved.", Toast.LENGTH_SHORT);
            unsavedToast.show();
        }
        drawDoodle.destroyDrawingCache();
    }

    // Set Doodle as wallpaper
    public void setCanvasWallPaper(View view){
        drawDoodle.setDrawingCacheEnabled(true);
        WallpaperManager wallpaperManager =
                WallpaperManager.getInstance(getApplicationContext());
        try{
            wallpaperManager.setBitmap(drawDoodle.getDrawingCache());
        } catch (IOException e){
            e.printStackTrace();
        }
        Toast setWallToast = Toast.makeText(getApplicationContext(),
                "Doodle set as Wall Paper!!!",Toast.LENGTH_SHORT);
        setWallToast.show();
        drawDoodle.destroyDrawingCache();
    }

    // Reset the Canvas
    public void clearCanvas(View view){
        final int NEW_CANVAS_COLOR = 0xFFFFFFFF;
        drawDoodle.resetCanvas();
        drawDoodle.setBackgroundColor(NEW_CANVAS_COLOR);
    }

    // Change Canvas Background color
    public void canvasColorClicked(View view){
        /* Show color picker dialog */
        colorPicker.show();

    /* Set a new Listener called when user click "select" */
        colorPicker.setCallback(new ColorPickerCallback() {
            @Override
            public void onColorChosen(@ColorInt int color) {
                //drawDoodle.invalidate();
                drawDoodle.setBackgroundColor(color);
                colorPicker.dismiss();
            }
        });
    }

    // Change paint color
    public void paintClicked(View view){
        //use chosen color
        if( view != currPaintColor ){
            //update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawDoodle.setColor(color);
            imgView.setImageResource(R.drawable.paint_selected);
            currPaintColor.setImageResource(R.drawable.paint);
            currPaintColor = (ImageButton)view;
        }
    }

    // Check and Request permissions
    public  boolean checkAndRequestPermissions() {
        // Declare required permissions
        /*int camerapermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int permissionLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);*/
        int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);


        List<String> listPermissionsNeeded = new ArrayList<>();
        // Enable check conditions as required
        /*if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }*/
        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        Log.v(TAG, "**********Permission callback called*******");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with the permissions
                /*perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);*/
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for all permissions
                    /*    perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED */
                    if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){

                        Log.v(TAG, "sms & location services permission granted");
                        // process the normal flow
                        Intent i = new Intent(MainActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.v(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        /* ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO) */
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    private void explain(String msg){
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.challenge.svakt.droiddoodle")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast showVersionToast = Toast.makeText(getApplicationContext(),
                    "Droid Doodle V.1.0",Toast.LENGTH_SHORT);
            showVersionToast.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
