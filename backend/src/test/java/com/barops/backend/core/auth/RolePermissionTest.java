package com.barops.backend.core.auth;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class RolePermissionTest {

    @Test
    void adminHasFullSprintOnePermissions() {
        assertThat(authoritiesFor(Role.ADMIN)).containsExactlyInAnyOrder(
                "ROLE_ADMIN",
                "PRODUCT_READ", "PRODUCT_WRITE", "PRODUCT_UPDATE", "PRODUCT_DELETE",
                "INGREDIENT_READ", "INGREDIENT_WRITE", "INGREDIENT_UPDATE", "INGREDIENT_DELETE",
                "RECIPE_READ", "RECIPE_WRITE", "RECIPE_UPDATE", "RECIPE_DELETE",
                "INVENTORY_READ", "INVENTORY_WRITE");
    }

    @Test
    void managerCannotDeleteSprintOneResources() {
        assertThat(authoritiesFor(Role.MANAGER)).contains(
                "PRODUCT_READ", "PRODUCT_WRITE", "PRODUCT_UPDATE",
                "INGREDIENT_READ", "INGREDIENT_WRITE", "INGREDIENT_UPDATE",
                "RECIPE_READ", "RECIPE_WRITE", "RECIPE_UPDATE",
                "INVENTORY_READ", "INVENTORY_WRITE");
        assertThat(authoritiesFor(Role.MANAGER)).doesNotContain(
                "PRODUCT_DELETE", "INGREDIENT_DELETE", "RECIPE_DELETE");
    }

    @Test
    void staffIsReadOnly() {
        assertThat(authoritiesFor(Role.STAFF)).containsExactlyInAnyOrder(
                "ROLE_STAFF", "PRODUCT_READ", "INGREDIENT_READ", "RECIPE_READ", "INVENTORY_READ");
    }

    private Set<String> authoritiesFor(Role role) {
        User user = new User();
        user.setRole(role);
        return user.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .collect(Collectors.toSet());
    }
}