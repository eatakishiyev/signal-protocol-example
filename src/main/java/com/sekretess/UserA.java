package com.sekretess;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.state.*;
import org.whispersystems.libsignal.state.impl.InMemoryIdentityKeyStore;
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore;
import org.whispersystems.libsignal.util.KeyHelper;

import java.util.List;

@Data

public class Alice extends User {
    private final String NAME = "alice";
    private final int DEVICE_ID = 5412;

    @Override
    public SignalProtocolAddress getSignalProtocolAddress() {
        return new SignalProtocolAddress(NAME, DEVICE_ID);
    }


    @Override
    public int getDeviceId() {
        return DEVICE_ID;
    }
}
