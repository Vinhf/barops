package com.barops.backend.core.auth;

import java.util.Set;

public enum Role {
    ADMIN,
    MANAGER,
    STAFF;

    public Set<Permission> getPermissions() {
        return switch (this) {
            case ADMIN -> Set.of(
                    Permission.PRODUCT_READ,
                    Permission.PRODUCT_WRITE,
                    Permission.PRODUCT_UPDATE,
                    Permission.PRODUCT_DELETE,
                    Permission.INGREDIENT_READ,
                    Permission.INGREDIENT_WRITE,
                    Permission.INGREDIENT_UPDATE,
                    Permission.INGREDIENT_DELETE
            );
            case MANAGER -> Set.of(
                    Permission.PRODUCT_READ,
                    Permission.PRODUCT_WRITE,
                    Permission.PRODUCT_UPDATE,
                    Permission.INGREDIENT_READ,
                    Permission.INGREDIENT_WRITE,
                    Permission.INGREDIENT_UPDATE
            );
            case STAFF -> Set.of(
                    Permission.PRODUCT_READ,
                    Permission.INGREDIENT_READ
            );
        };
    }
}