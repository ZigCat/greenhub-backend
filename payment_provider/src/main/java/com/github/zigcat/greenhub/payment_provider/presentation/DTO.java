package com.github.zigcat.greenhub.payment_provider.presentation;

public class DTO {
    public record ApiError(
            String message,
            int status
    ){};
}
