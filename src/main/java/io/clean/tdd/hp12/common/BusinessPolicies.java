package io.clean.tdd.hp12.common;

public interface BusinessPolicies {

    int TOKEN_ACTIVATION_CAPACITY = 50;
    int TOKEN_ACTIVATION_ORDER_DIFFERENCE_TOLERANCE = 50;

    int WAITING_TOKEN_DURATION_MINUTES = 30;
    int ACTIVE_TOKEN_DURATION_MINUTES = 15;

    int EXPIRATION_SCHEDULING_INTERVAL_SECONDS = 3;
    int TEMPORARY_RESERVATION_DURATION_MINUTES = 5;
    int TEMPORARY_RESERVATION_ABOLISH_DEFER_MINUTES = 1;
}
