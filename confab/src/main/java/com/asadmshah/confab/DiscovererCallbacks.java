package com.asadmshah.confab;

/**
 * Callbacks to be used with {@link Discoverer}.
 */
public interface DiscovererCallbacks extends BaseConnectionCallbacks {

    /**
     * Called when a {@link AdvertiserEndpoint} is found and a connection request can be sent to it.
     *
     * @param endpoint that is advertising.
     */
    void onAdvertiserEndpointFound(AdvertiserEndpoint endpoint);

}
