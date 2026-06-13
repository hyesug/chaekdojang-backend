package com.chaekdojang.api.domain.library.dto;

import com.chaekdojang.api.domain.library.LibraryStatus;
import jakarta.validation.constraints.NotNull;

public record LibraryUpdateRequest(@NotNull LibraryStatus status) {
}
