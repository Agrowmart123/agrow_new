package com.agrowmart.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

    @NotBlank(message = "Old password is required")
    String oldPassword,

    @NotBlank(message = "New password is required")
    @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters")
    String newPassword,

    @NotBlank(message = "Confirm password is required")
    String confirmNewPassword

) {
    public boolean passwordsMatch() {
        return newPassword != null && newPassword.equals(confirmNewPassword);
    }
}