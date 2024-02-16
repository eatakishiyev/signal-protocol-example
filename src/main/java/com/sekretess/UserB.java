package com.sekretess;

import org.whispersystems.libsignal.*;


public class UserB extends User {
    private final String NAME = "userB";
    private final int DEVICE_ID = 2;


    @Override
    public SignalProtocolAddress getSignalProtocolAddress() {
        return new SignalProtocolAddress(NAME, DEVICE_ID);
    }

    @Override
    public int getDeviceId() {
        return DEVICE_ID;
    }
}
