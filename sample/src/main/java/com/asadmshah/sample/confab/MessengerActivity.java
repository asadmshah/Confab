package com.asadmshah.sample.confab;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

public abstract  class MessengerActivity extends AppCompatActivity {

    private ArrayAdapter<String> listAdapter;
    private EditText editText;
    private ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1));
        editText = (EditText) findViewById(R.id.message);
        sendButton = (ImageButton) findViewById(R.id.send);
    }

    @Override
    protected void onResume() {
        super.onResume();
        disableViews();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableViews();
    }

    protected void enableViews() {
        editText.setEnabled(true);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(editText.getText().toString().getBytes());
            }
        });
    }

    protected void disableViews() {
        editText.setEnabled(false);
        sendButton.setOnClickListener(null);
    }

    protected void onEndpointConnected(String name) {
        listAdapter.add(name + " has connected");
    }

    protected void onEndpointMessage(String name, byte[] message) {
        listAdapter.add("From " + name + ": " + new String(message));
    }

    protected void onEndpointDisconnected(String name) {
        listAdapter.add(name + " has disconnected");
    }

    abstract void sendMessage(byte[] message);
}
