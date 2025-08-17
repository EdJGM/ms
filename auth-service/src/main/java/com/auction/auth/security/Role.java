package com.auction.auth.security;

public enum Role {
    ADMIN("ADMINISTRADOR"),
    MODERADOR("MODERADOR"),
    PARTICIPANTE("PARTICIPANTE");

    private final String value;

    Role(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Role fromString(String role) {
        if (role == null) {
            return PARTICIPANTE; // Default role
        }
        
        for (Role r : Role.values()) {
            if (r.value.equalsIgnoreCase(role)) {
                return r;
            }
        }
        return PARTICIPANTE; // Default role
    }

    @Override
    public String toString() {
        return value;
    }
}
