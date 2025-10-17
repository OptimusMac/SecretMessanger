package ru.optimus.client.messengerclient.utils;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.security.SecureRandom;
import java.util.Arrays;

public class TwofishCrypto {

    private final SecureRandom random = new SecureRandom();

    @SneakyThrows
    public byte[] encrypt(byte[] plaintext, byte[] key)  {
        byte[] iv = new byte[16];
        random.nextBytes(iv);

        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new TwofishEngine()));
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
        cipher.init(true, params);

        byte[] output = new byte[cipher.getOutputSize(plaintext.length)];
        int len = cipher.processBytes(plaintext, 0, plaintext.length, output, 0);
        len += cipher.doFinal(output, len);

        byte[] result = new byte[iv.length + len];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(output, 0, result, iv.length, len);
        return result;
    }

    public static byte[] decrypt(byte[] ciphertext, byte[] key) throws Exception {
        byte[] iv = Arrays.copyOfRange(ciphertext, 0, 16);
        byte[] actualCipher = Arrays.copyOfRange(ciphertext, 16, ciphertext.length);

        PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new TwofishEngine()));
        CipherParameters params = new ParametersWithIV(new KeyParameter(key), iv);
        cipher.init(false, params);

        byte[] output = new byte[cipher.getOutputSize(actualCipher.length)];
        int len = cipher.processBytes(actualCipher, 0, actualCipher.length, output, 0);
        len += cipher.doFinal(output, len);

        return Arrays.copyOf(output, len);
    }

}
