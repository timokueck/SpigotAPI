package me.TechsCode.SpigotAPI.server;

import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

public class TwoFactorAuth {

    private final String secretKey;

    public TwoFactorAuth(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getCode() {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

}
