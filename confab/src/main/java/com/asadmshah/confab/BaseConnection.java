package com.asadmshah.confab;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.Connections;
import com.google.android.gms.nearby.connection.Connections.MessageListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a base and any shared methods between the Advertiser and Discoverer.
 */
abstract class BaseConnection<S extends Endpoint, T extends BaseConnectionCallbacks> {

    /** Listeners for any base connection callbacks. */
    protected final List<T> callbackListeners = new ArrayList<>();

    /** Keeps track of all endpoints, connected or not. */
    protected final Map<String, S> endpointsMap = new HashMap<>();

    protected final GoogleApiClient connection;
    protected final String serviceId;
    protected final String localEndpointName;
    protected long timeout;

    /**
     * Base constructor that prepares a {@link GoogleApiClient} to be used with the Nearby Connections
     * API.
     *
     * @param context to be used in {@link com.google.android.gms.common.api.GoogleApiClient}.
     * @param serviceId for the service to advertise or discover
     * @param localEndpointName is an optional name for your device. This value gets provided when
     *                          remote endpoints call {@link Endpoint#getName()}
     */
    BaseConnection(@NonNull Context context, @NonNull String serviceId, @Nullable String localEndpointName) {
        this.connection = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(apiConnectionCallbacks)
                .addOnConnectionFailedListener(apiConnectionFailedListener)
                .addApi(Nearby.CONNECTIONS_API)
                .build();
        this.serviceId = serviceId;
        this.localEndpointName = localEndpointName;
    }

    /**
     * Starts a connection with the timeout set to indefinite.
     *
     * See {@link #start(long)}
     */
    public void start() {
        start(Connections.DURATION_INDEFINITE);
    }

    /**
     * Connects to the {@link GoogleApiClient}. When connected (or already connected)
     * {@link BaseConnection#onBaseConnectionReady()} gets called
     *
     * @param timeout in milliseconds to keep discovering/advertising for.
     */
    public void start(long timeout) {
        this.timeout = timeout;

        if (connection.isConnected()) {
            onBaseConnectionReady();
        } else if (!connection.isConnecting()) {
            connection.connect();
        }
    }

    /**
     * Disconnects from all endpoints and the Google Api connection is lost.
     */
    public void disconnect() {
        if (connection.isConnected() || connection.isConnecting()) {
            Nearby.Connections.stopAllEndpoints(connection);
            connection.disconnect();
        }
    }

    /**
     * Called when a successful connection is made to {@link com.google.android.gms.common.api.GoogleApiClient}
     * in order to start discovering or advertising.
     */
    protected void onBaseConnectionReady() {

    }

    /**
     * For {@link GoogleApiClient#registerConnectionCallbacks(ConnectionCallbacks)}
     */
    private final ConnectionCallbacks apiConnectionCallbacks = new ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            onBaseConnectionReady();
        }

        @Override
        public void onConnectionSuspended(int i) {
            for (T listener : callbackListeners) {
                listener.onConnectionSuspended(connection, i);
            }
        }
    };

    /**
     * For {@link GoogleApiClient#registerConnectionFailedListener(OnConnectionFailedListener)}
     */
    private final OnConnectionFailedListener apiConnectionFailedListener = new OnConnectionFailedListener() {
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            for (T listener : callbackListeners) {
                listener.onConnectionFailed(connection, connectionResult);
            }
        }
    };

    /** Listens to messages, or disconnects from an endpoint. */
     protected final MessageListener messageListener = new MessageListener() {
        @Override
        public void onMessageReceived(String endpointId, byte[] data, boolean isReliable) {
            Endpoint endpoint = endpointsMap.get(endpointId);
            if (endpoint != null) {
                endpoint.onMessage(data, isReliable);
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            S endpoint = endpointsMap.remove(endpointId);
            if (endpoint != null) {
                endpoint.onDisconnected();
            }
        }
    };

    /**
     * Called when a fatal error occurs that disconnects the entire connection and notifies any
     * listeners.
     *
     * @param error that occurred.
     */
    protected final void onFatalErrorOccurred(@NonNull ConfabError error) {
        connection.disconnect();

        Collection<S> endpoints = endpointsMap.values();
        endpointsMap.clear();
        for (S endpoint : endpoints) {
            endpoint.onDisconnected();
        }

        for (T listener : callbackListeners) {
            listener.onConnectionFailed(error);
        }
        callbackListeners.clear();
    }

    void sendReliableMessage(@NonNull Endpoint endpoint, @Size(max=4096) byte[] message) {
        Nearby.Connections.sendReliableMessage(connection, endpoint.getId(), message);
    }

    void sendUnreliableMessage(@NonNull Endpoint endpoint, @Size(max=1168) byte[] data) {
        Nearby.Connections.sendUnreliableMessage(connection, endpoint.getId(), data);
    }

    void disconnectFromEndpoint(@NonNull Endpoint endpoint) {
        Nearby.Connections.disconnectFromEndpoint(connection, endpoint.getId());
    }

    /**
     * Adds a callback listener in {@link BaseConnection#callbackListeners} if not previously added.
     *
     * @param listener to add.
     */
    public void addCallbackListener(T listener) {
        if (!callbackListeners.contains(listener)) {
            callbackListeners.add(listener);
        }
    }

    /**
     * Removes a callback listener from {@link BaseConnection#callbackListeners} if exists.
     *
     * @param listener to remove.
     */
    public void removeCallbackListener(T listener) {
        callbackListeners.remove(listener);
    }

}
