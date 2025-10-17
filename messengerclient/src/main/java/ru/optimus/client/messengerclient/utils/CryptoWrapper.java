package ru.optimus.client.messengerclient.utils;

import org.bouncycastle.util.encoders.Base64;

public class CryptoWrapper {
    private final TwofishCrypto twofish;

    public CryptoWrapper(TwofishCrypto twofish){
        this.twofish = twofish;
    }


    public String encode(byte[] bytes){
        return Base64.toBase64String(bytes);
    }
}
