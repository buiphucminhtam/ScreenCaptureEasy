package com.minhtam.screencaptureeasy.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.minhtam.screencaptureeasy.Adapter.AdapterImage;
import com.minhtam.screencaptureeasy.Interface.OnItemClickListener;
import com.minhtam.screencaptureeasy.R;
import com.minhtam.screencaptureeasy.Util.SharedPreferencesManager;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterImage adapterImage;
    private List<String> listPathImage;
    private LinearLayoutManager layoutManager;
    private ImageView imvMain;
    private SharedPreferencesManager sharedPreferencesManager;
    private int currentPositionImage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        setTheme();
        setContentView(R.layout.activity_image_viewer);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.rvListImages);
        imvMain = (ImageView) findViewById(R.id.imvMain);
        listPathImage = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        adapterImage = new AdapterImage(this, listPathImage);
        getAllImages();

        adapterImage.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                currentPositionImage = position;
                imvMain.setImageURI(Uri.fromFile(new File(listPathImage.get(position))));
            }
        });

    }

    private void setTheme() {
        String[] arrayTheme = getResources().getStringArray(R.array.themeValues);

        if (sharedPreferencesManager.getThemeType().equals(arrayTheme[0])) {
            setTheme(R.style.LightTheme);
        } else if (sharedPreferencesManager.getThemeType().equals(arrayTheme[1])) {
            setTheme(R.style.DarkTheme);
        } else {
            if (sharedPreferencesManager.getThemeType().equals(arrayTheme[2])) {
                setTheme(R.style.BlackTheme);
            }
        }
    }

    private void getAllImages() {
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        String directoryPath = sharedPreferencesManager.getSaveLocation();
        // List all the items within the folder.
        File[] files = new File(directoryPath).listFiles(new ImageFileFilter());

        for (File file : files) {
            if (isImageFile(file.getAbsolutePath())) {
                listPathImage.add(file.getAbsolutePath());
            }
        }

        Collections.reverse(listPathImage);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapterImage);
        imvMain.setImageURI(Uri.fromFile(new File(listPathImage.get(0))));
    }

    private boolean isImageFile(String filePath) {
        if (filePath.endsWith(".jpg") || filePath.endsWith(".png"))
        // Add other formats as desired
        {
            return true;
        }
        return false;
    }

    private class ImageFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return false;
            }
            else if (isImageFile(file.getAbsolutePath())) {
                return true;
            }
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(1,1,1,"CropImage");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        item.setIcon(R.drawable.ic_crop_20px);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            CropImage.activity(Uri.fromFile(new File(listPathImage.get(currentPositionImage))))
                    .start(this);
//            CropImage.activity()
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .start(this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        this.finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Log.d("CROPIMAGE", "result: " + resultUri.getPath());
                //Add to list image and save it to location
                listPathImage.add(0,writeImageUriToFile(resultUri));
                adapterImage.notifyDataSetChanged();
                //Scroll to first
                recyclerView.scrollToPosition(0);
                //Set to image view main
                imvMain.setImageURI(Uri.fromFile(new File(listPathImage.get(0))));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private String writeImageUriToFile(Uri imgUri) {
        final int chunkSize = 1024;  // We'll read in one kB at a time
        byte[] imageData = new byte[chunkSize];

        try {
            DateFormat dateFormat = new SimpleDateFormat(sharedPreferencesManager.getFileName());
            Date date = new Date();
            InputStream in = getContentResolver().openInputStream(imgUri);
            String path = sharedPreferencesManager.getSaveLocation() + File.separator + dateFormat.format(date) + ".png";
            OutputStream out = new FileOutputStream(new File(path));  // I'm assuming you already have the File object for where you're writing to

            int bytesRead;
            while ((bytesRead = in.read(imageData)) > 0) {
                out.write(Arrays.copyOfRange(imageData, 0, Math.max(0, bytesRead)));
            }

            in.close();
            out.close();

            return path;
        } catch (Exception ex) {
            Log.e("Something went wrong.", ex.getMessage());
            return "";
        }
    }
}
