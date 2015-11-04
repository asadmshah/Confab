package com.asadmshah.sample.confab;

import android.os.Bundle;
import android.util.Log;

import com.asadmshah.confab.AdvertiserEndpoint;
import com.asadmshah.confab.AdvertiserEndpointCallbacks;
import com.asadmshah.confab.ConfabError;
import com.asadmshah.confab.Discoverer;
import com.asadmshah.confab.DiscovererCallbacks;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class DiscovererActivity extends MessengerActivity implements DiscovererCallbacks, AdvertiserEndpointCallbacks {

    public static final String TAG = DiscovererActivity.class.getSimpleName();

    private Discoverer discoverer;
    private AdvertiserEndpoint endpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        discoverer = new Discoverer(this, getString(R.string.service_id), "Discoverer");
    }

    @Override
    protected void onResume() {
        super.onResume();
        discoverer.addCallbackListener(this);
        discoverer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearEndpoint();
        discoverer.disconnect();
        discoverer.removeCallbackListener(this);
    }

    @Override
    void sendMessage(byte[] message) {
        if (endpoint != null) {
            endpoint.sendReliableMessage(message);
        }
    }

    @Override
    public void onAdvertiserEndpointFound(AdvertiserEndpoint endpoint) {
        Log.d(TAG, "onAdvertiserEndpointFound: " + endpoint.getName());
        this.endpoint = endpoint;
        this.endpoint.addCallbackListener(this);
        this.endpoint.sendConnectionRequest("This is my connection request".getBytes());
    }

    @Override
    public void onConnectionSuspended(GoogleApiClient client, int i) {
        Log.e(TAG, "onConnectionSuspended: " + i);
        clearEndpoint();
    }

    @Override
    public void onConnectionFailed(GoogleApiClient client, ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: " + connectionResult.getErrorMessage());
        clearEndpoint();
    }

    @Override
    public void onConnectionFailed(ConfabError error) {
        Log.e(TAG, "onConnectionFailed: " + error.getMessage());
        clearEndpoint();
    }

    @Override
    public void onEndpointConnectionRequestFailed(AdvertiserEndpoint endpoint, ConfabError error) {
        Log.e(TAG, "onEndpointConnectionRequestFailed: " + error.getMessage());
        clearEndpoint();
    }

    @Override
    public void onEndpointConnected(AdvertiserEndpoint endpoint, byte[] data) {
        onEndpointConnected(endpoint.getName());
        enableViews();
    }

    @Override
    public void onEndpointMessage(AdvertiserEndpoint endpoint, byte[] data, boolean isReliable) {
        onEndpointMessage(endpoint.getName(), data);
    }

    @Override
    public void onEndpointDisconnected(AdvertiserEndpoint endpoint) {
        onEndpointDisconnected(endpoint.getName());
    }

    @Override
    public void onEndpointError(AdvertiserEndpoint endpoint, ConfabError error) {
        Log.e(TAG, "onEndpointError: " + error.getMessage());
        clearEndpoint();
    }

    private void clearEndpoint() {
        if (endpoint != null) {
            endpoint.removeCallbackListener(this);
            endpoint = null;
        }
    }
}
