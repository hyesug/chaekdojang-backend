package com.chaekingam.api.domain.library;

import com.chaekingam.api.domain.library.dto.LibraryAddRequest;
import com.chaekingam.api.domain.library.dto.LibraryResponse;
import com.chaekingam.api.domain.library.dto.LibraryUpdateRequest;
import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "내 서재", description = "읽는 중·완독·위시리스트 관리")
@RestController
@RequestMapping("/api/library")
@RequiredArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;

    @Operation(summary = "서재에 도서 추가", description = "읽는 중·완독·위시리스트 중 하나의 상태로 도서를 서재에 추가합니다. JWT 필요.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<LibraryResponse> add(@RequestBody @Valid LibraryAddRequest request) {
        return ApiResponse.ok(libraryService.add(request));
    }

    @Operation(summary = "내 서재 조회", description = "내 서재 목록을 반환합니다. status 파라미터로 READING·FINISHED·WISHLIST 필터링 가능. JWT 필요.")
    @GetMapping
    public ApiResponse<List<LibraryResponse>> getMyLibrary(
            @Parameter(description = "상태 필터 (READING·FINISHED·WISHLIST). 생략 시 전체 반환")
            @RequestParam(required = false) LibraryStatus status) {
        return ApiResponse.ok(libraryService.getMyLibrary(status));
    }

    @Operation(summary = "서재 상태 변경", description = "서재에 담긴 도서의 읽기 상태를 변경합니다. JWT 필요.")
    @PatchMapping("/{id}")
    public ApiResponse<LibraryResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid LibraryUpdateRequest request) {
        return ApiResponse.ok(libraryService.updateStatus(id, request));
    }

    @Operation(summary = "서재에서 도서 삭제", description = "서재에서 도서를 제거합니다. JWT 필요.")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remove(@PathVariable Long id) {
        libraryService.remove(id);
    }
}
