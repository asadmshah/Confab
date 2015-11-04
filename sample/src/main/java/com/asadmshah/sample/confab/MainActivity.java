package com.asadmshah.sample.confab;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startAdvertiserActivity(View view) {
        startActivity(new Intent(this, AdvertiserActivity.class));
    }

    public void startDiscovererActivity(View view) {
        startActivity(new Intent(this, DiscovererActivity.class));
    }

}
