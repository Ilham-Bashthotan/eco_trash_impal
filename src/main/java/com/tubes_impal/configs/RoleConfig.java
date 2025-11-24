package com.tubes_impal.configs;

import com.tubes_impal.entity.UserRole;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Role-based access control configuration
 * Defines permissions for each role without using queries
 */
@Component
public class RoleConfig {

    private static final Map<UserRole, List<String>> rolePermissions = new HashMap<>();

    static {
        // Admin permissions
        rolePermissions.put(UserRole.ADMIN, List.of(
            "USER_READ",
            "USER_WRITE",
            "USER_DELETE",
            "COURIER_MANAGE",
            "ORDER_VIEW_ALL",
            "ORDER_MANAGE",
            "STATS_VIEW_ALL"
        ));

        // Seller permissions
        rolePermissions.put(UserRole.SELLER, List.of(
            "ORDER_CREATE",
            "ORDER_VIEW_OWN",
            "ORDER_CANCEL_OWN",
            "BALANCE_VIEW_OWN",
            "TRASH_CREATE"
        ));

        // Courier permissions
        rolePermissions.put(UserRole.COURIER, List.of(
            "ORDER_VIEW_ASSIGNED",
            "ORDER_UPDATE_STATUS",
            "ORDER_ACCEPT",
            "STATS_VIEW_OWN"
        ));
    }

    public boolean hasPermission(UserRole role, String permission) {
        List<String> permissions = rolePermissions.get(role);
        return permissions != null && permissions.contains(permission);
    }

    public boolean canAccessOrder(UserRole role, Integer orderSellerId, Integer orderCourierId, Integer userId) {
        return switch (role) {
            case ADMIN -> true;
            case SELLER -> orderSellerId != null && orderSellerId.equals(userId);
            case COURIER -> orderCourierId != null && orderCourierId.equals(userId);
        };
    }

    public boolean canManageCourier(UserRole role) {
        return role == UserRole.ADMIN;
    }

    public boolean canCreateOrder(UserRole role) {
        return role == UserRole.SELLER;
    }

    public boolean canUpdateOrderStatus(UserRole role) {
        return role == UserRole.COURIER || role == UserRole.ADMIN;
    }

    public boolean canViewAllOrders(UserRole role) {
        return role == UserRole.ADMIN;
    }

    public boolean canViewBalance(UserRole role, Integer sellerId, Integer userId) {
        if (role == UserRole.ADMIN) {
            return true;
        }
        if (role == UserRole.SELLER) {
            return sellerId.equals(userId);
        }
        return false;
    }
}
