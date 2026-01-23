package com.agrowmart.util;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import com.agrowmart.util.InMemoryOtpStore;

import com.agrowmart.enums.OtpPurpose;

public class InMemoryOtpStore {

    private static final ConcurrentHashMap<String, OtpData> store = new ConcurrentHashMap<>();

    public static void saveOtp(String phone, String otp, OtpPurpose purpose, int expirySeconds) {
        store.put(key(phone, purpose),
            new OtpData(otp, LocalDateTime.now().plusSeconds(expirySeconds)));
    }

    public static OtpData getOtp(String phone, OtpPurpose purpose) {
        return store.get(key(phone, purpose));
    }

    public static void removeOtp(String phone, OtpPurpose purpose) {
        store.remove(key(phone, purpose));
    }

    private static String key(String phone, OtpPurpose purpose) {
        return phone + "_" + purpose;
    }

    public record OtpData(String otp, LocalDateTime expiresAt) {}

	public static Object getOtp(String normalizedPhone, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	public static void removeOtp(String normalizedPhone, String name) {
		// TODO Auto-generated method stub
		
	}

	public static void saveOtp(String normalizedPhone, String code, String name, int expirySeconds) {
		// TODO Auto-generated method stub
		
	}
}
