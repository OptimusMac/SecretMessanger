package ru.optimus.client.messengerclient.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.Base64;

public class GlobalKeyReceiver {

    @Setter
    @Getter
    private static byte[] key;
    public static boolean inSession = false;

    public static String encrypt(byte[] message, CryptoWrapper cryptoWrapper, TwofishCrypto twofishCrypto) {
        String decrypt = new String(message);

        if (key == null || !inSession) {
            return decrypt;
        }
        return cryptoWrapper.encode(twofishCrypto.encrypt(message, key));
    }


}
