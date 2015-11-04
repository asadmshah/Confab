package com.asadmshah.confab;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Connections.ConnectionResponseCallback;
import com.google.android.gms.nearby.connection.Connections.EndpointDiscoveryListener;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

/**
 * Allows discovery of any remote {@link AdvertiserEndpoint}
 */
public class Discoverer extends BaseConnection<AdvertiserEndpoint, DiscovererCallbacks> {

    /**
     * Creates a new Discoverer endpoint from this device.
     *
     * @param context to be used in {@link com.google.android.gms.common.api.GoogleApiClient}.
     * @param serviceId for the service to discover
     * @param localEndpointName is an optional name for your device. This value gets provided when
     *                          remote endpoints call {@link Endpoint#getName()}
     */
    public Discoverer(Context context, String serviceId, String localEndpointName) {
        super(context, serviceId, localEndpointName);
    }

    /**
     * Stops discovering for any remote endpoints with {@param serviceId}. Communication can continue
     * between any connected endpoints, discovering new endpoints will cease.
     */
    public void stopDiscovering() {
        Nearby.Connections.stopDiscovery(connection, serviceId);
    }

    /**
     * {@inheritDoc}
     *
     * Starts discovering when this is called.
     */
    @Override
    protected void onBaseConnectionReady() {
        super.onBaseConnectionReady();
        Nearby.Connections.startDiscovery(connection, serviceId, timeout, endpointDiscoveryListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        int statusCode = status.getStatusCode();
                        switch (statusCode) {
                            case ConnectionsStatusCodes.STATUS_OK:
                                break;
                            case ConnectionsStatusCodes.STATUS_ALREADY_DISCOVERING:
                                break;
                            case ConnectionsStatusCodes.STATUS_NETWORK_NOT_CONNECTED:
                                onFatalErrorOccurred(new ConfabError(statusCode));
                                break;
                        }
                    }
                });
    }

    /**
     * Send a connection request to an {@link AdvertiserEndpoint} with an optional message.
     *
     * @param endpoint to send a connection request to.
     * @param message to send alongside with the request. This is optional and can be null.
     */
    void sendConnectionRequest(@NonNull AdvertiserEndpoint endpoint, @Nullable byte[] message) {
        Nearby.Connections.sendConnectionRequest(connection, localEndpointName, endpoint.getId(), message, connectionResponseCallback, messageListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        int statusCode = status.getStatusCode();
                        switch (statusCode) {
                            case ConnectionsStatusCodes.STATUS_OK:
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

    private final ConnectionResponseCallback connectionResponseCallback = new ConnectionResponseCallback() {
        @Override
        public void onConnectionResponse(String endpointId, Status status, byte[] message) {
            Endpoint endpoint = endpointsMap.get(endpointId);
            if (endpoint != null) {
                int statusCode = status.getStatusCode();
                switch (statusCode) {
                    case ConnectionsStatusCodes.STATUS_OK:
                        endpoint.onConnected(message);
                        break;
                    case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                        endpoint.onError(new ConfabError(statusCode));
                        break;
                    case ConnectionsStatusCodes.STATUS_NOT_CONNECTED_TO_ENDPOINT:
                        endpoint.onError(new ConfabError(statusCode));
                        break;
                }
            }
        }
    };

    private final EndpointDiscoveryListener endpointDiscoveryListener = new EndpointDiscoveryListener() {
        @Override
        public void onEndpointFound(String endpointId, String deviceId, String serviceId, String name) {
            if (serviceId.equals(Discoverer.this.serviceId)) {
                AdvertiserEndpoint endpoint = new AdvertiserEndpoint(Discoverer.this, endpointId, deviceId, name);
                endpointsMap.put(endpointId, endpoint);
                for (DiscovererCallbacks listener : callbackListeners) {
                    listener.onAdvertiserEndpointFound(endpoint);
                }
            }
        }

        @Override
        public void onEndpointLost(String endpointId) {
            AdvertiserEndpoint endpoint = endpointsMap.remove(endpointId);
            if (endpoint != null) {
                endpoint.onDisconnected();
            }
        }
    };

}
