package com.sekretess;

import lombok.Data;
import org.signal.libsignal.protocol.SignalProtocolAddress;

@Data

public class UserA extends User {
    private final String NAME = "userA";
    private final int DEVICE_ID = 1;

    @Override
    public SignalProtocolAddress getSignalProtocolAddress() {
        return new SignalProtocolAddress(NAME, DEVICE_ID);
    }


    @Override
    public int getDeviceId() {
        return DEVICE_ID;
    }
}
