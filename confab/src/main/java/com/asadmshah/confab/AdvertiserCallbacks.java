package com.asadmshah.confab;

/**
 * Callbacks to be used with {@link Advertiser}.
 */
public interface AdvertiserCallbacks extends BaseConnectionCallbacks {

    /**
     * Called when a {@link DiscovererEndpoint} sends a connection request.
     *
     * @param endpoint requesting a connection.
     * @param message sent alongside the request. This can be null.
     */
    void onDiscovererEndpointConnectionRequest(DiscovererEndpoint endpoint, byte[] message);

}
