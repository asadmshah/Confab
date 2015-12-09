package com.asadmshah.confab;

import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;

/**
 * Errors from {@link ConnectionsStatusCodes}
 */
public final class ConfabError extends Error {

    private final int errorCode;

    /**
     *
     * @param errorCode must be one of {@link ConnectionsStatusCodes}
     */
    ConfabError(int errorCode) {
        super(ConnectionsStatusCodes.getStatusCodeString(errorCode));
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

}
