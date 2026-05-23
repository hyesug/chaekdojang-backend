package com.chaekingam.api.domain.library.dto;

import com.chaekingam.api.domain.library.LibraryStatus;

public record LibraryBookStatusResponse(
        boolean inLibrary,
        LibraryStatus status,
        Long libraryId
) {}
