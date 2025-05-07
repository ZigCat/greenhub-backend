package com.github.zigcat.greenhub.payment_provider.presentation;

public class PresentationDTO {
    public record ApiError(
            String message,
            int status
    ){};
}
