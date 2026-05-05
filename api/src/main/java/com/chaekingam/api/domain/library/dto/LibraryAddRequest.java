package com.chaekingam.api.domain.library.dto;

import com.chaekingam.api.domain.library.LibraryStatus;
import jakarta.validation.constraints.NotNull;

public record LibraryAddRequest(
        @NotNull Long bookId,
        @NotNull LibraryStatus status
) {
}
