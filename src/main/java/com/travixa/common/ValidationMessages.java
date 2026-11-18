package com.travixa.common;

public final class ValidationMessages {
    public static final String REQUIRED = "This field is required.";
    public static final String INVALID_EMAIL = "Please provide a valid email address.";
    public static final String INVALID_PHONE = "Please provide a valid phone number.";
    public static final String INVALID_PAGE = "Page index must be greater than or equal to zero.";
    public static final String INVALID_SIZE = "Page size must be greater than zero.";
    public static final String INVALID_AMOUNT = "Amount must be greater than or equal to zero.";
    public static final String INVALID_STATUS = "Status is invalid.";
    public static final String INVALID_DATE_RANGE = "Start date must be before end date.";
    public static final String NAME_REQUIRED = "Name is required.";
    public static final String TITLE_REQUIRED = "Title is required.";
    public static final String ID_REQUIRED = "Id is required.";

    private ValidationMessages() {
        throw new IllegalStateException("Utility class");
    }
}
