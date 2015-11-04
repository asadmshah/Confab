package com.asadmshah.confab;

interface EndpointCallbacks<T extends Endpoint> {
    void onEndpointConnectionRequestFailed(T endpoint, ConfabError error);
    void onEndpointConnected(T endpoint, byte[] data);
    void onEndpointMessage(T endpoint, byte[] data, boolean isReliable);
    void onEndpointDisconnected(T endpoint);
    void onEndpointError(T endpoint, ConfabError error);
}
