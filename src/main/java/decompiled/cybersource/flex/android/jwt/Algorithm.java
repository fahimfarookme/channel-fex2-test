package decompiled.cybersource.flex.android.jwt;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.MGF1ParameterSpec;
import java.util.Arrays;

public enum Algorithm {
    HS256("HS256") {
        @Override
        byte[] encrypt(byte[] key, byte[] iv, byte[] payload, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] decrypt(byte[] key, byte[] iv, byte[] cipherText, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] sign(Key secretKey, String payload) {
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(secretKey);
                return mac.doFinal(payload.getBytes(Tools.UTF8));
            } catch (GeneralSecurityException gse) {
                throw new IllegalArgumentException(gse); // Exception here will be argument(s) related
            }
        }

        @Override
        boolean verify(Key secretKey, String payload, byte[] signature) {
            try {
                Mac mac = Mac.getInstance("HmacSHA256");
                mac.init(secretKey);
                return Arrays.equals(mac.doFinal(payload.getBytes(Tools.UTF8)), signature);
            } catch (GeneralSecurityException gse) {
                throw new IllegalArgumentException(gse); // Exception here will be argument(s) related
            }
        }
    },
    /**
     * RSASSA-PKCS1-v1_5 using SHA-256
     */
    RSA256("RS256") {
        @Override
        byte[] encrypt(byte[] key, byte[] iv, byte[] payload, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] decrypt(byte[] key, byte[] iv, byte[] cipherText, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] sign(Key privateKey, String payload) {
            return sign("SHA256withRSA", (PrivateKey) privateKey, payload);
        }

        @Override
        boolean verify(Key publicKey, String payload, byte[] signature) {
            return verify("SHA256withRSA", (PublicKey) publicKey, payload, signature);
        }
    },
    /**
     * RSASSA-PKCS1-v1_5 using SHA-512
     */
    RSA512("RS512") {
        @Override
        byte[] encrypt(byte[] key, byte[] iv, byte[] payload, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] decrypt(byte[] key, byte[] iv, byte[] cipherText, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] sign(Key privateKey, String payload) {
            return sign("SHA512withRSA", (PrivateKey) privateKey, payload);
        }

        @Override
        boolean verify(Key publicKey, String payload, byte[] signature) {
            return verify("SHA512withRSA", (PublicKey) publicKey, payload, signature);
        }
    },
    /**
     * RSAES OAEP using default parameters
     */
    RSAOAEP("RSA-OAEP") {
        @Override
        byte[] encrypt(byte[] key, byte[] iv, byte[] payload, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] encrypt(PublicKey publicKey, byte[] cipherText) {
            if (publicKey == null) {
                throw new IllegalArgumentException("publicKey is null");
            }
            if (cipherText == null) {
                throw new IllegalArgumentException("cipherText can't be null");
            }

            try {
                final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
                cipher.init(Cipher.ENCRYPT_MODE, publicKey);
                return cipher.doFinal(cipherText);
            } catch (GeneralSecurityException | RuntimeException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        byte[] decrypt(byte[] key, byte[] iv, byte[] cipherText, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] decrypt(PrivateKey privateKey, byte[] cipherText) {
            try {
                final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-1AndMGF1Padding");
                cipher.init(Cipher.DECRYPT_MODE, privateKey);
                return cipher.doFinal(cipherText);
            } catch (GeneralSecurityException | RuntimeException e) {
                throw new IllegalStateException(e);
            }
        }
    },
    /**
     * RSAES OAEP using SHA-256 and MGF1 with SHA-256
     */
    RSAOAEP256("RSA-OAEP-256") {
        @Override
        byte[] encrypt(byte[] key, byte[] iv, byte[] payload, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] encrypt(PublicKey publicKey, byte[] cipherText) {
            if (publicKey == null) {
                throw new IllegalArgumentException("publicKey is null");
            }
            if (cipherText == null) {
                throw new IllegalArgumentException("cipherText is null");
            }

            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
                OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
                cipher.init(Cipher.ENCRYPT_MODE, publicKey, oaepParams);
                return cipher.doFinal(cipherText);
            } catch (GeneralSecurityException | RuntimeException e) {
                throw new IllegalStateException(e);
            }
        }

        @Override
        byte[] decrypt(byte[] key, byte[] iv, byte[] cipherText, byte[] aad) {
            throw new IllegalStateException();
        }

        @Override
        byte[] decrypt(PrivateKey privateKey, byte[] cipherText) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
                OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1", new MGF1ParameterSpec("SHA-256"), PSource.PSpecified.DEFAULT);
                cipher.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
                return cipher.doFinal(cipherText);
            } catch (GeneralSecurityException | RuntimeException e) {
                throw new IllegalStateException(e);
            }
        }
    },
    /**
     * AES GCM using 128-bit key
     */
    A128GCM("A128GCM") {
        @Override
        byte[] aesKey() {
            return nonce(128 / 8);
        }
    },
    /**
     * AES GCM using 256-bit key
     */
    A256GCM("A256GCM") {
        @Override
        byte[] aesKey() {
            return nonce(256 / 8);
        }
    },;

    final String jwa; // JSON Web Algorithm (JWA)

    Algorithm(String jwa) {
        this.jwa = jwa;
    }

    static Algorithm match(String algo) {
        for (Algorithm a : values()) {
            if (a.jwa.equals(algo)) {
                return a;
            }
        }
        throw new IllegalArgumentException("Unsupported algorithm");
    }

    byte[] aesKey() {
        throw new IllegalStateException();
    }

    byte[] encrypt(byte[] key, byte[] iv, byte[] payload, byte[] aad) {
        if (key == null) {
            throw new IllegalArgumentException("AES encryption key is null");
        }
        if (iv == null) {
            throw new IllegalArgumentException("IV is null");
        }
        if (payload == null) {
            throw new IllegalArgumentException("payload is null");
        }
        if (aad == null) {
            throw new IllegalArgumentException("aad is null");
        }

        try {
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
            cipher.updateAAD(aad);
            return cipher.doFinal(payload);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    byte[] encrypt(PublicKey publicKey, byte[] cipherText) {
        throw new IllegalStateException();
    }

    byte[] decrypt(byte[] key, byte[] iv, byte[] cipherText, byte[] aad) {
        if (key == null) {
            throw new IllegalArgumentException("AES encryption key is null");
        }
        if (iv == null) {
            throw new IllegalArgumentException("IV is null");
        }
        if (cipherText == null) {
            throw new IllegalArgumentException("payload is null");
        }
        if (aad == null) {
            throw new IllegalArgumentException("aad is null");
        }

        try {
            SecretKey secretKey = new SecretKeySpec(key, "AES");
            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec parameterSpec = new GCMParameterSpec(128, iv); //128 bit auth tag length
            cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
            cipher.updateAAD(aad);
            return cipher.doFinal(cipherText);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }
    }

    byte[] decrypt(PrivateKey privateKey, byte[] cipherText) {
        throw new IllegalStateException();
    }

    byte[] sign(Key privateKey, String payload) {
        throw new IllegalStateException();
    }

    boolean verify(Key publicKey, String payload, byte[] signature) {
        throw new IllegalStateException();
    }

    protected static byte[] nonce(int noOfBytes) {
        final byte[] nonce = new byte[noOfBytes];
        Tools.PRNG.nextBytes(nonce);
        return nonce;
    }

    protected static byte[] sign(final String algo, final PrivateKey privateKey, final String payload) {
        try {
            final Signature signInstance = Signature.getInstance(algo);
            signInstance.initSign(privateKey);
            signInstance.update(payload.getBytes(Tools.UTF8));
            return signInstance.sign();
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException("Cannot sign: " + e.getMessage(), e);
        }
    }

    protected static boolean verify(final String algo, final PublicKey publicKey, final String payload, final byte[] signature) {
        try {
            final Signature signInstance = Signature.getInstance(algo);
            signInstance.initVerify(publicKey);
            signInstance.update(payload.getBytes(Tools.UTF8));
            return signInstance.verify(signature);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
            throw new RuntimeException("Cannot verify: " + e.getMessage(), e);
        }
    }

}
