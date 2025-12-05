package com.example.demo.config;

import java.util.UUID;

public class TenantContext {
    private static final ThreadLocal<UUID> currentCenter = new ThreadLocal<>();

    public static void setCenterId(UUID centerId) {
        currentCenter.set(centerId);
    }

    public static UUID getCenterId() {
        return currentCenter.get();
    }

    public static void clear() {
        currentCenter.remove();
    }
}
