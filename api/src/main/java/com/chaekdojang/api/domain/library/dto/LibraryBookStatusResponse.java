package com.chaekdojang.api.domain.library.dto;

import com.chaekdojang.api.domain.library.LibraryStatus;

public record LibraryBookStatusResponse(
        boolean inLibrary,
        LibraryStatus status,
        Long libraryId
) {}
