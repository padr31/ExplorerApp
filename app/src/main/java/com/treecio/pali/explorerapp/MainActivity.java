package com.treecio.pali.explorerapp;

import android.Manifest;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.io.File;

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
        String path = prefs.getString(KEY_PREF_DEFAULT_DIRECTORY, "");
        if (path.equals("")) {
            currentPath = Environment.getExternalStorageDirectory().toString();
        } else {
            currentPath = path;
        }

        refresh();

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
        transaction.replace(R.id.fragment_container, newFragment).commit();


    }

    public String[] getFileNames(String path){

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

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
    }
}
