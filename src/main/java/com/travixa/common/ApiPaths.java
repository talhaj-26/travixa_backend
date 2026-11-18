package com.travixa.common;

public final class ApiPaths {
    public static final String ROOT = "/api";
    public static final String V1 = "/v1";

    public static final String AUTH = ROOT + V1 + "/auth";
    public static final String USERS = ROOT + V1 + "/users";
    public static final String BOOKINGS = ROOT + V1 + "/bookings";
    public static final String HOTELS = ROOT + V1 + "/hotels";
    public static final String RESTAURANTS = ROOT + V1 + "/restaurants";
    public static final String TRANSPORT = ROOT + V1 + "/transport";
    public static final String TOURS = ROOT + V1 + "/tours";
    public static final String PARTNERS = ROOT + V1 + "/partners";
    public static final String PAYMENTS = ROOT + V1 + "/payments";
    public static final String CMS = ROOT + V1 + "/cms";
    public static final String SUPPORT = ROOT + V1 + "/support";
    public static final String ANALYTICS = ROOT + V1 + "/analytics";
    public static final String CART = ROOT + V1 + "/cart";
    public static final String CHECKOUT = ROOT + V1 + "/checkout";
    public static final String AI = ROOT + V1 + "/ai";

    private ApiPaths() {
        throw new IllegalStateException("Utility class");
    }
}
