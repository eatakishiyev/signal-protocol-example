package com.sekretess;

import lombok.Getter;
import lombok.Setter;
import org.signal.libsignal.protocol.*;
import org.signal.libsignal.protocol.ecc.Curve;
import org.signal.libsignal.protocol.ecc.ECKeyPair;
import org.signal.libsignal.protocol.state.PreKeyBundle;
import org.signal.libsignal.protocol.state.PreKeyRecord;
import org.signal.libsignal.protocol.state.SignalProtocolStore;
import org.signal.libsignal.protocol.state.SignedPreKeyRecord;
import org.signal.libsignal.protocol.state.impl.InMemorySignalProtocolStore;
import org.signal.libsignal.protocol.util.KeyHelper;
import org.signal.libsignal.protocol.util.Medium;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
public abstract class User {
    private IdentityKeyPair identityKeyPair;
    private int registrationId;
    private SignedPreKeyRecord signedPreKeyRecord;

    private SignalProtocolStore signalProtocolStore;
    private Integer signedPreKeyId;
    private List<PreKeyRecord> preKeyRecords = new ArrayList<>();

    public void initializeKeys() throws InvalidKeyException {
        this.signedPreKeyId = new Random().nextInt(Medium.MAX_VALUE - 1);
        int startIdx = 0;
        //Generate Identity Key Pair
        ECKeyPair ecKeyPair = Curve.generateKeyPair();
        IdentityKey identityKey = new IdentityKey(ecKeyPair.getPublicKey());
        this.identityKeyPair = new IdentityKeyPair(identityKey, ecKeyPair.getPrivateKey());

        //Generate registration id
        this.registrationId = KeyHelper.generateRegistrationId(false);
        this.signalProtocolStore = new InMemorySignalProtocolStore(identityKeyPair, registrationId);

        this.signedPreKeyRecord = generateSignedPreKey(identityKeyPair, signedPreKeyId);

        generateSignedPreKeys(100);
        signalProtocolStore.storeSignedPreKey(signedPreKeyRecord.getId(), signedPreKeyRecord);

    }

    private void generateSignedPreKeys(int count) {
        SecureRandom preKeyRecordIdGenerator = new SecureRandom();
        for (int i = 0; i < count; i++) {
            int id = preKeyRecordIdGenerator.nextInt(Integer.MAX_VALUE);
            PreKeyRecord preKeyRecord = new PreKeyRecord(id, Curve.generateKeyPair());
            signalProtocolStore.storePreKey(id, preKeyRecord);
            preKeyRecords.add(preKeyRecord);
        }
    }

    public PreKeyBundle generatePreKeyBundle(int preKeyIndex) throws InvalidKeyException {
        PreKeyRecord preKeyRecord = preKeyRecords.get(preKeyIndex);
        return new PreKeyBundle(this.registrationId,
                this.getDeviceId(),
                preKeyRecord.getId(),
                preKeyRecord.getKeyPair().getPublicKey(),
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

        sessionBuilder.process(user.generatePreKeyBundle(4));
        SessionCipher sessionCipher = new SessionCipher(signalProtocolStore, user.getSignalProtocolAddress());
        return sessionCipher;
    }

    private SignedPreKeyRecord generateSignedPreKey(IdentityKeyPair identityKeyPair, int signedPreKeyId) throws InvalidKeyException {
        ECKeyPair keyPair = Curve.generateKeyPair();
        byte[] signature = Curve.calculateSignature(identityKeyPair.getPrivateKey(), keyPair.getPublicKey().serialize());
        return new SignedPreKeyRecord(signedPreKeyId, System.currentTimeMillis(), keyPair, signature);
    }


}
