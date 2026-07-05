package co.com.gestorfranquicia.api.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateNameRequest(@NotBlank String name) {
}
