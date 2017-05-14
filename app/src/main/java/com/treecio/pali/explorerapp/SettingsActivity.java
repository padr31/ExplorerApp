package com.treecio.pali.explorerapp;

import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;

public class SettingsActivity extends AppCompatActivity {

    public static final String DEFAULT_DIRECTORY = "default_directory";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        /*TextView textView = (TextView) findViewById(R.id.text_view);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String path = prefs.getString(SettingsActivity.DEFAULT_DIRECTORY, "");
        textView.setText(getString(R.string.dir_current) + path);
        */
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
