package com.treecio.pali.explorerapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.net.URLConnection;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextFragment.OnFragmentInteractionListener, ListFragment.OnFragmentInteractionListener{

    private static final String KEY_PREF_DEFAULT_DIRECTORY = "defalut";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    public String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String path = prefs.getString(SettingsActivity.DEFAULT_DIRECTORY, "");

        if (new File(path).isDirectory()){
            currentPath = path;
        } else {
            currentPath = Environment.getExternalStorageDirectory().toString();
        }
        refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.item_refresh:
                refresh();
                return true;
            case R.id.item_settings:
                startSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void refresh() {

        new Thread(new Runnable(){
            @Override
            public void run() {
                String[] names = getFileNames(currentPath);
                Fragment newFragment;
                if(names == null) {
                    newFragment = TextFragment.newInstance("Can't access directory: " + currentPath);
                }
                else if(names.length == 0) {
                    newFragment = TextFragment.newInstance("Directory is empty: " + currentPath);
                }
                else {
                    newFragment = ListFragment.newInstance(names);
                }
                changeFragment(newFragment);
            }
        }).start();

    }

    private void changeFragment(Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.fragment_container, newFragment).commit();


    }

    public String[] getFileNames(String path){

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

        File[] files = new File(path).listFiles();
        String[] fileNames;
        if(files == null)
            return null;
        else {
            fileNames = new String[files.length];

            for (int i=0; i<files.length; i++)
                fileNames[i] = files[i].getName();
        }

        return fileNames;
    }

    public void onBackPressed() {
        File p = new File(currentPath).getParentFile();
        if(p == null || p == Environment.getExternalStorageDirectory().getParentFile()) {
            return;
        } else {
            changeDirectory(p.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void changeDirectory(String path) {
        currentPath = path;
        refresh();
    }

    public void openFile(String path) {
        File f=new File(path);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(f),getMimeType(f.getAbsolutePath()));
        Intent j = Intent.createChooser(intent, "Choose an application to open with:");
        startActivity(j);
    }

    private String getMimeType(String url)
    {
        String parts[]=url.split("\\.");
        String extension=parts[parts.length-1];
        String type = null;
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
