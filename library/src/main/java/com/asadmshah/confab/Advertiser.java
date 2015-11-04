package com.asadmshah.confab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AppIdentifier;
import com.google.android.gms.nearby.connection.AppMetadata;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.Connections.ConnectionRequestListener;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows advertising this endpoint so a {@link DiscovererEndpoint} can connect to it.
 */
public class Advertiser extends BaseConnection<DiscovererEndpoint, AdvertiserCallbacks> {

    /**
     * Creates a new Advertiser endpoint from this device.
     *
     * @param context to be used in {@link com.google.android.gms.common.api.GoogleApiClient}.
     * @param serviceId for the service to advertise
     * @param localEndpointName is an optional name for your device. This value gets provided when
     *                          remote endpoints call {@link Endpoint#getName()}
     */
    public Advertiser(@NonNull Context context, @NonNull String serviceId, @Nullable String localEndpointName) {
        super(context, serviceId, localEndpointName);
    }

    /**
     * Stop advertising for any remote endpoints. Communication can continue between connected endpoints.
     */
    public void stopAdvertising() {
        Nearby.Connections.stopAdvertising(connection);
    }

    /**
     * {@inheritDoc}
     *
     * Starts advertising when this is called.
     */
    @Override
    protected void onBaseConnectionReady() {
        super.onBaseConnectionReady();
        List<AppIdentifier> appIdentifierList = new ArrayList<>();
        appIdentifierList.add(new AppIdentifier(connection.getContext().getPackageName()));
        AppMetadata appMetadata = new AppMetadata(appIdentifierList);

        Nearby.Connections.startAdvertising(connection, localEndpointName, appMetadata, timeout, connectionRequestListener)
                .setResultCallback(new ResultCallback<Connections.StartAdvertisingResult>() {
                    @Override
                    public void onResult(Connections.StartAdvertisingResult startAdvertisingResult) {
                        int statusCode = startAdvertisingResult.getStatus().getStatusCode();
                        switch (statusCode) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                break;
                            case ConnectionsStatusCodes.STATUS_ALREADY_ADVERTISING:
                                break;
                            case ConnectionsStatusCodes.STATUS_NETWORK_NOT_CONNECTED:
                                onFatalErrorOccurred(new ConfabError(statusCode));
                                break;
                            case ConnectionsStatusCodes.STATUS_ERROR:
                                onFatalErrorOccurred(new ConfabError(statusCode));
                                break;
                        }
                    }
                });
    }

    /**
     * Accept a connection request from an {@link DiscovererEndpoint}.
     *
     * @param endpoint requesting a connection.
     * @param data is an optional field that the requesting endpoint can send alongside.
     */
    void acceptConnectionRequest(@NonNull final DiscovererEndpoint endpoint, @Nullable byte[] data) {
        Nearby.Connections.acceptConnectionRequest(connection, endpoint.getId(), data, messageListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        int statusCode = status.getStatusCode();
                        switch (statusCode) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                endpoint.onConnected(null);
                                break;
                            case ConnectionsStatusCodes.STATUS_ALREADY_CONNECTED_TO_ENDPOINT:
                                break;
                            case ConnectionsStatusCodes.STATUS_NETWORK_NOT_CONNECTED:
                                onFatalErrorOccurred(new ConfabError(statusCode));
                                break;
                        }
                    }
                });
    }

    /**
     * Reject a connection request from an {@link DiscovererEndpoint}. This blocks any future
     * connection requests. If the endpoint is already connected, a successful rejection will not
     * occur.
     *
     * @param endpoint to reject the connection from.
     */
    void rejectConnectionRequest(@NonNull final DiscovererEndpoint endpoint) {
        Nearby.Connections.rejectConnectionRequest(connection, endpoint.getId())
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        int statusCode = status.getStatusCode();
                        switch (statusCode) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                endpoint.onDisconnected();
                                break;
                            case ConnectionsStatusCodes.STATUS_ALREADY_CONNECTED_TO_ENDPOINT:
                                // TODO: Should this disconnect?
                                break;
                            case ConnectionsStatusCodes.STATUS_NETWORK_NOT_CONNECTED:
                                onFatalErrorOccurred(new ConfabError(statusCode));
                                break;
                        }
                    }
                });
    }

    private final ConnectionRequestListener connectionRequestListener = new ConnectionRequestListener() {
        @Override
        public void onConnectionRequest(String endpointId, String deviceId, String endpointName, byte[] data) {
            DiscovererEndpoint endpoint = new DiscovererEndpoint(Advertiser.this, endpointId, deviceId, endpointName);
            endpointsMap.put(endpointId, endpoint);
            for (AdvertiserCallbacks callbacks : callbackListeners) {
                callbacks.onDiscovererEndpointConnectionRequest(endpoint, data);
            }
        }
    };

}
