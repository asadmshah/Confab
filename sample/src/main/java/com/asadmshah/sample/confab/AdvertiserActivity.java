package com.asadmshah.sample.confab;

import android.os.Bundle;
import android.util.Log;

import com.asadmshah.confab.Advertiser;
import com.asadmshah.confab.AdvertiserCallbacks;
import com.asadmshah.confab.ConfabError;
import com.asadmshah.confab.DiscovererEndpoint;
import com.asadmshah.confab.DiscovererEndpointCallbacks;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public class AdvertiserActivity extends MessengerActivity implements AdvertiserCallbacks, DiscovererEndpointCallbacks {

    public static final String TAG = AdvertiserActivity.class.getSimpleName();

    private Advertiser advertiser;
    private DiscovererEndpoint endpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        advertiser = new Advertiser(this, getString(R.string.service_id), "Advertiser");
    }

    @Override
    protected void onResume() {
        super.onResume();
        advertiser.addCallbackListener(this);
        advertiser.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearEndpoint();
        advertiser.disconnect();
        advertiser.removeCallbackListener(this);
    }

    @Override
    void sendMessage(byte[] message) {
        if (endpoint != null) {
            endpoint.sendReliableMessage(message);
        }
    }

    @Override
    public void onDiscovererEndpointConnectionRequest(DiscovererEndpoint endpoint, byte[] data) {
        Log.d(TAG, "onDiscovererEndpointConnectionRequest: " + endpoint.getName() + ",data: " + new String(data));
        this.endpoint = endpoint;
        this.endpoint.addCallbackListener(this);
        this.endpoint.acceptConnectionRequest("This is me accepting".getBytes());
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
    public void onEndpointConnectionRequestFailed(DiscovererEndpoint endpoint, ConfabError error) {
        Log.e(TAG, "onEndpointConnectionRequestFailed: " + error.getMessage());
        clearEndpoint();
    }

    @Override
    public void onEndpointConnected(DiscovererEndpoint endpoint, byte[] data) {
        onEndpointConnected(endpoint.getName());
        enableViews();
    }

    @Override
    public void onEndpointMessage(DiscovererEndpoint endpoint, byte[] data, boolean isReliable) {
        onEndpointMessage(endpoint.getName(), data);
    }

    @Override
    public void onEndpointDisconnected(DiscovererEndpoint endpoint) {
        onEndpointDisconnected(endpoint.getName());
        disableViews();
        clearEndpoint();
    }

    @Override
    public void onEndpointError(DiscovererEndpoint endpoint, ConfabError error) {
        Log.e(TAG, "onEndpointError: " + error.getMessage());
        disableViews();
        clearEndpoint();
    }

    private void clearEndpoint() {
        if (endpoint != null) {
            endpoint.removeCallbackListener(this);
            endpoint = null;
        }
    }
}
