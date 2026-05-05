package com.chaekingam.api.domain.library;

import com.chaekingam.api.domain.library.dto.LibraryAddRequest;
import com.chaekingam.api.domain.library.dto.LibraryResponse;
import com.chaekingam.api.domain.library.dto.LibraryUpdateRequest;
import com.chaekingam.api.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LibraryResponse> add(@RequestBody @Valid LibraryAddRequest request) {
        return ApiResponse.ok(libraryService.add(request));
    }

    @GetMapping
    public ApiResponse<List<LibraryResponse>> getMyLibrary(
            @RequestParam(required = false) LibraryStatus status) {
        return ApiResponse.ok(libraryService.getMyLibrary(status));
    }

    @PatchMapping("/{id}")
    public ApiResponse<LibraryResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid LibraryUpdateRequest request) {
        return ApiResponse.ok(libraryService.updateStatus(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        libraryService.remove(id);
    }
}
