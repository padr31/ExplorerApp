package com.treecio.pali.explorerapp;

import android.content.SharedPreferences;
import android.os.Environment;
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
        setContentView(R.layout.activity_settings);

        TextView textView = (TextView) findViewById(R.id.text_view);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String path = prefs.getString(SettingsActivity.DEFAULT_DIRECTORY, "");
        textView.setText(getString(R.string.dir_current) + path);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void setPath(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_text);
        TextView textView = (TextView) findViewById(R.id.text_view);

        String path = editText.getText().toString();
        File f = new File(path);
        if(f.isDirectory()) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(DEFAULT_DIRECTORY, path);
            editor.commit();

            textView.setText(R.string.dir_updated);
        } else {
            textView.setText(R.string.dir_invalid);
        }

    }
}
