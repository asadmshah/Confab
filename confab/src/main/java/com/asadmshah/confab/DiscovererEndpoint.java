package com.asadmshah.confab;

import android.support.annotation.Nullable;

/**
 * An endpoint presenting itself as a Discoverer.
 */
public class DiscovererEndpoint extends Endpoint<DiscovererEndpointCallbacks, Advertiser> {

    DiscovererEndpoint(Advertiser advertiser, String id, String deviceId, String name) {
        super(advertiser, id, deviceId, name);
    }

    /**
     * Accepts the connection request from this endpoint. An optional message may be provided
     * alongside. Once a request is successfully accepted, a callback will be issued through
     * {@link DiscovererEndpointCallbacks#onEndpointConnected(Endpoint, byte[])}
     *
     * @param data to send alongside the response. This is optional and can be null.
     */
    public void acceptConnectionRequest(@Nullable byte[] data) {
        this.issuer.acceptConnectionRequest(this, data);
    }

    /**
     * Rejects the connection request from this endpoint. This endpoint will no longer be able to
     * send connection requests to you.
     */
    public void rejectConnectionRequest() {
        this.issuer.rejectConnectionRequest(this);
    }

    @Override
    void onConnectionRequestFailed(ConfabError error) {
        super.onConnectionRequestFailed(error);
        for (DiscovererEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointConnectionRequestFailed(this, error);
        }
    }

    @Override
    void onConnected(byte[] data) {
        super.onConnected(data);
        for (DiscovererEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointConnected(this, data);
        }
    }

    @Override
    void onMessage(byte[] data, boolean isReliable) {
        super.onMessage(data, isReliable);
        for (DiscovererEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointMessage(this, data, isReliable);
        }
    }

    @Override
    void onDisconnected() {
        super.onDisconnected();
        for (DiscovererEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointDisconnected(this);
        }
    }

    @Override
    void onError(ConfabError error) {
        super.onError(error);
        for (DiscovererEndpointCallbacks callbacks : callbackListeners) {
            callbacks.onEndpointError(this, error);
        }
    }
}
