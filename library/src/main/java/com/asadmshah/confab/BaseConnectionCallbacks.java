package com.asadmshah.confab;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

interface BaseConnectionCallbacks {
    void onConnectionSuspended(GoogleApiClient client, int i);
    void onConnectionFailed(GoogleApiClient client, ConnectionResult connectionResult);
    void onConnectionFailed(ConfabError error);
}
