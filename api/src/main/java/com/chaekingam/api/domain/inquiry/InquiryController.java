package com.chaekingam.api.domain.inquiry;

import com.chaekingam.api.domain.inquiry.dto.InquiryCreateRequest;
import com.chaekingam.api.domain.inquiry.dto.InquiryResponse;
import com.chaekingam.api.global.response.ApiResponse;
import com.chaekingam.api.global.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping
    public ResponseEntity<ApiResponse<InquiryResponse>> create(
            @Valid @RequestBody InquiryCreateRequest req,
            Authentication auth) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(inquiryService.create(req, userId)));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<InquiryResponse>>> my(
            Authentication auth,
            @RequestParam(required = false) String email) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(inquiryService.getMyInquiries(userId, email)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<InquiryResponse>> detail(
            @PathVariable Long id,
            Authentication auth,
            @RequestParam(required = false) String email) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(inquiryService.getDetail(id, userId, email)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<InquiryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody InquiryCreateRequest req,
            Authentication auth,
            @RequestParam(required = false) String email) {
        Long userId = getUserId(auth);
        return ResponseEntity.ok(ApiResponse.ok(inquiryService.update(id, req, userId, email)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            Authentication auth,
            @RequestParam(required = false) String email) {
        Long userId = getUserId(auth);
        inquiryService.delete(id, userId, email);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    private Long getUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        try { return (Long) auth.getPrincipal(); } catch (Exception e) { return null; }
    }
}
