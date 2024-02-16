package com.sekretess;

import org.signal.libsignal.protocol.*;
import org.signal.libsignal.protocol.message.CiphertextMessage;
import org.signal.libsignal.protocol.message.PreKeySignalMessage;


import java.io.UnsupportedEncodingException;

public class Main {
    public static void main(String[] args) throws InvalidKeyException, UntrustedIdentityException,
            UnsupportedEncodingException, InvalidMessageException, DuplicateMessageException, InvalidKeyIdException,
            LegacyMessageException, InvalidVersionException, NoSessionException {
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
