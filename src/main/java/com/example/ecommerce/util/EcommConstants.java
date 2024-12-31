package com.example.ecommerce.util;

public class EcommConstants {

    /** Constants of roles id. */
    public static final Long ROLE_ADMIN_ID = 1L;
    public static final Long ROLE_USER_ID = 2L;

    /** Constants of roles name. */
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_USER = "USER";

    /** PayPal Constants */
    public static final String PAYMENT_SUCCESS_URL = "http://localhost:8080/payment/success";
    public static final String PAYMENT_CANCEL_URL = "http://localhost:8080/payment/cancel";
    private static final String PAYMENT_INTENT = "sale";
    private static final String PAYMENT_METHOD = "paypal";
}
