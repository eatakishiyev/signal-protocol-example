package com.sekretess;

import lombok.Data;
import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.protocol.CiphertextMessage;
import org.whispersystems.libsignal.state.*;
import org.whispersystems.libsignal.state.impl.InMemoryIdentityKeyStore;
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore;
import org.whispersystems.libsignal.util.KeyHelper;

import java.util.List;


public class Bob extends User {
    private final String NAME = "bob";
    private final int DEVICE_ID = 4310;


    @Override
    public SignalProtocolAddress getSignalProtocolAddress() {
        return new SignalProtocolAddress(NAME, DEVICE_ID);
    }

    @Override
    public int getDeviceId() {
        return DEVICE_ID;
    }
}
