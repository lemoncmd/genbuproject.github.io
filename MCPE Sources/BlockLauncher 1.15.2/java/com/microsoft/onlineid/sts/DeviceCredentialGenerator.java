package com.microsoft.onlineid.sts;

import java.security.SecureRandom;

public class DeviceCredentialGenerator {
    static final String LegalPasswordCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-_=+[]{}/?;:'\",.<>`~";
    static final String LegalUsernameCharacters = "abcdefghijklmnopqrstuvwxyz";
    static final String LogicalDevicePrefix = "02";
    static final int PasswordLength = 16;
    static final int UsernameLength = 20;
    private final SecureRandom _randomNumberGenerator;

    public DeviceCredentialGenerator() {
        this._randomNumberGenerator = new SecureRandom();
    }

    DeviceCredentialGenerator(SecureRandom secureRandom) {
        this._randomNumberGenerator = secureRandom;
    }

    private String generateRandomString(String str, int i) {
        char[] cArr = new char[i];
        int length = str.length();
        for (int i2 = 0; i2 < cArr.length; i2++) {
            cArr[i2] = str.charAt(this._randomNumberGenerator.nextInt(length));
        }
        return new String(cArr);
    }

    public DeviceCredentials generate() {
        return new DeviceCredentials(LogicalDevicePrefix + generateRandomString(LegalUsernameCharacters, 20 - LogicalDevicePrefix.length()), generateRandomString(LegalPasswordCharacters, PasswordLength));
    }
}
