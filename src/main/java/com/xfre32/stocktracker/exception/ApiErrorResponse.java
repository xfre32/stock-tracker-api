package com.xfre32.stocktracker.exception;

public record ApiErrorResponse(int status, String message) {}