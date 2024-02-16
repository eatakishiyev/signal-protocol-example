package com.sekretess;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.ecc.ECKeyPair;
import org.whispersystems.libsignal.state.PreKeyBundle;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignalProtocolStore;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.state.impl.InMemorySignalProtocolStore;
import org.whispersystems.libsignal.util.KeyHelper;
import org.whispersystems.libsignal.util.Medium;

import java.util.List;
import java.util.Random;

@Getter
@Setter
public abstract class User {
    private IdentityKeyPair identityKeyPair;
    private int registrationId;
    private List<PreKeyRecord> preKeyRecords;
    private SignedPreKeyRecord signedPreKeyRecord;

    private SignalProtocolStore signalProtocolStore;
    private Integer signedPreKeyId;
    private PreKeyBundle preKeyBundle;

    public void initializeKeys() throws InvalidKeyException {
        this.signedPreKeyId = new Random().nextInt(Medium.MAX_VALUE - 1);
        int startIdx = 0;
        this.identityKeyPair = KeyHelper.generateIdentityKeyPair();
        this.registrationId = KeyHelper.generateRegistrationId(false);
        this.preKeyRecords = KeyHelper.generatePreKeys(startIdx, 100);
        this.signedPreKeyRecord = KeyHelper.generateSignedPreKey(identityKeyPair, signedPreKeyId);

        this.signalProtocolStore = new InMemorySignalProtocolStore(identityKeyPair, registrationId);
        for (PreKeyRecord preKeyRecord : preKeyRecords) {
            signalProtocolStore.storePreKey(preKeyRecord.getId(), preKeyRecord);
        }
        signalProtocolStore.storeSignedPreKey(signedPreKeyRecord.getId(), signedPreKeyRecord);

        this. preKeyBundle = new PreKeyBundle(this.registrationId,
                this.getDeviceId(),
                this.preKeyRecords.get(0).getId(),
                this.preKeyRecords.get(0).getKeyPair().getPublicKey(),
                this.signedPreKeyId,
                this.signedPreKeyRecord.getKeyPair().getPublicKey(),
                this.signedPreKeyRecord.getSignature(),
                this.identityKeyPair.getPublicKey());
    }


    abstract int getDeviceId();

    abstract SignalProtocolAddress getSignalProtocolAddress();
    public final SessionCipher buildSessionWith(User user) throws UntrustedIdentityException, InvalidKeyException {
        SignalProtocolStore signalProtocolStore = getSignalProtocolStore();

        SessionBuilder sessionBuilder = new SessionBuilder(signalProtocolStore, user.getSignalProtocolAddress());

        sessionBuilder.process(user.getPreKeyBundle());
        SessionCipher sessionCipher = new SessionCipher(signalProtocolStore, user.getSignalProtocolAddress());
        return sessionCipher;
    }


}
