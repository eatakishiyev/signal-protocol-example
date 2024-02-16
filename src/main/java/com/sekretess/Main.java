package com.sekretess;

import org.whispersystems.libsignal.*;
import org.whispersystems.libsignal.ecc.Curve;
import org.whispersystems.libsignal.protocol.CiphertextMessage;
import org.whispersystems.libsignal.protocol.PreKeySignalMessage;

import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) throws InvalidKeyException, UntrustedIdentityException, UnsupportedEncodingException, InvalidMessageException, DuplicateMessageException, InvalidKeyIdException, LegacyMessageException, InvalidVersionException {
        UserA userA = new UserA();
        userA.initializeKeys();

        UserB userB = new UserB();
        userB.initializeKeys();

        SessionCipher encoder = userB.buildSessionWith(userA);
        CiphertextMessage encrypt = encoder.encrypt("HelloWorld".getBytes("UTF-8"));
        System.out.println(encrypt);

        SessionCipher decoder = userA.buildSessionWith(userB);
        byte[] decrypt = decoder.decrypt(new PreKeySignalMessage(encrypt.serialize()));
        System.out.println(new String(decrypt));


    }
}
