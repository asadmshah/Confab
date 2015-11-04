package com.asadmshah.confab;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Base/common functionality amongst the specific endpoints.
 */
public abstract class Endpoint<S, T extends BaseConnection> {

    protected final List<S> callbackListeners = new ArrayList<>();
    protected final T issuer;
    protected final String id;
    protected final String deviceId;
    protected final String name;
    protected boolean connected = false;

    Endpoint(@NonNull T issuer, @NonNull String id, @NonNull String deviceId, @Nullable String name) {
        this.issuer = issuer;
        this.id = id;
        this.deviceId = deviceId;
        this.name = name;
    }

    /**
     * Returns a unique identifier for this endpoint. Most of the NearbyConnections API uses this
     * as an endpoint identity.
     *
     * @return the unique endpoint id.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns another unique identifier for this endpoint.
     *
     * @return the unique device id.
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Returns a user provided, likely human-readable name for this Endpoint.
     * {@link BaseConnection#BaseConnection(Context, String, String)} sets this up. If null is passed,
     * the device automatically makes one up for you. ie: "LGE Nexus 5"
     *
     * @return a human readable name.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns whether the endpoint is connected.
     *
     * @return whether the endpoint is connected.
     */
    public boolean isConnected() {
        return connected;
    }

    @CallSuper
    void onConnectionRequestFailed(ConfabError error) {
        connected = false;
    }

    @CallSuper
    void onConnected(byte[] data) {
        connected = true;
    }

    void onMessage(byte[] data, boolean isReliable) {

    }

    @CallSuper
    void onDisconnected() {
        connected = false;
    }

    @CallSuper
    void onError(ConfabError error) {
        connected = false;
    }

    /**
     * Sends a reliable message to this endpoint.
     *
     * @param data to send. Size cannot exceed 4096 bytes.
     */
    public void sendReliableMessage(@Size(max = 4096) byte[] data) {
        this.issuer.sendReliableMessage(this, data);
    }

    /**
     * Sends an unreliable message to this endpoint.
     *
     * @param data to send. Size cannot exceed 1168 bytes.
     */
    public void sendUnreliableMessage(@Size(max = 1168) byte[] data) {
        this.issuer.sendUnreliableMessage(this, data);
    }

    /**
     * Disconnect from this endpoint. A callback through {@link EndpointCallbacks#onEndpointDisconnected(Endpoint)}
     * will let you know whether the disconnect was successful.
     */
    public void disconnect() {
        this.issuer.disconnectFromEndpoint(this);
    }

    /**
     * Adds a callback listener to be notified of any future events.
     *
     * @param listener to add.
     */
    public void addCallbackListener(S listener) {
        if (!callbackListeners.contains(listener)) {
            callbackListeners.add(listener);
        }
    }

    /**
     * Removes a callback listener from receiving any future events.
     *
     * @param listener to remove.
     */
    public void removeCallbackListener(S listener) {
        callbackListeners.remove(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endpoint endpoint = (Endpoint) o;
        return id.equals(endpoint.getId())
                && deviceId.equals(endpoint.getDeviceId())
                && name.equals(endpoint.getName());
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new String[]{id, deviceId, name});
    }

    @Override
    public String toString() {
        return "id:" + id + "," + "deviceId:" + deviceId + "," + "name:" + name;
    }

}
