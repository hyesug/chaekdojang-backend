package com.chaekingam.api.domain.library.dto;

import com.chaekingam.api.domain.library.LibraryStatus;
import jakarta.validation.constraints.NotNull;

public record LibraryUpdateRequest(@NotNull LibraryStatus status) {
}
