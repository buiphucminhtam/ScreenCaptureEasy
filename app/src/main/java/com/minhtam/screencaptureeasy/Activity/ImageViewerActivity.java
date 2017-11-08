package com.minhtam.screencaptureeasy.Activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.minhtam.screencaptureeasy.Adapter.AdapterImage;
import com.minhtam.screencaptureeasy.Interface.OnItemClickListener;
import com.minhtam.screencaptureeasy.R;
import com.minhtam.screencaptureeasy.Util.SharedPreferencesManager;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AdapterImage adapterImage;
    private List<String> listPathImage;
    private LinearLayoutManager layoutManager;
    private ImageView imvMain;
    private SharedPreferencesManager sharedPreferencesManager;

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
                listPathImage.add(0,file.getAbsolutePath());
            }
        }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,MainActivity.class));
        this.finish();
    }
}
