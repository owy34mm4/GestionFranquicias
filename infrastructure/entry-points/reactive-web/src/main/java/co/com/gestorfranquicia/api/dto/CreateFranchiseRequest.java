package co.com.gestorfranquicia.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseRequest(@NotBlank String name) {
}
