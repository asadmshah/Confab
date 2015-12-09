package com.asadmshah.confab;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An endpoint presenting itself as an Advertiser.
 */
public class AdvertiserEndpoint extends Endpoint<AdvertiserEndpointCallbacks, Discoverer> {

    AdvertiserEndpoint(@NonNull Discoverer issuer, @NonNull String id, @NonNull String deviceId, @Nullable String name) {
        super(issuer, id, deviceId, name);
    }

    /**
     * Send a connection request to this advertiser endpoint. An optional message may be provided
     * alongside. Once a connection is successfully accepted, a callback will be issued through
     * {@link AdvertiserEndpointCallbacks#onEndpointConnected(Endpoint, byte[])}
     *
     * @param data to send alongside the request. This is optional and can be null.
     */
    public void sendConnectionRequest(@Nullable byte[] data) {
        issuer.sendConnectionRequest(this, data);
    }

    @Override
    void onConnectionRequestFailed(ConfabError error) {
        super.onConnectionRequestFailed(error);
        for (AdvertiserEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointConnectionRequestFailed(this, error);
        }
    }

    @Override
    void onConnected(byte[] data) {
        super.onConnected(data);
        for (AdvertiserEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointConnected(this, data);
        }
    }

    @Override
    void onMessage(byte[] data, boolean isReliable) {
        for (AdvertiserEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointMessage(this, data, isReliable);
        }
    }

    @Override
    void onDisconnected() {
        super.onDisconnected();
        for (AdvertiserEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointDisconnected(this);
        }
    }

    @Override
    void onError(ConfabError error) {
        super.onError(error);
        for (AdvertiserEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointError(this, error);
        }
    }

}
