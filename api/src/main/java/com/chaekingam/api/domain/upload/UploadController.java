package com.chaekingam.api.domain.upload;

import com.chaekingam.api.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Tag(name = "파일 업로드", description = "이미지 업로드")
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    private static final String UPLOAD_DIR = "uploads/profile-images";

    @Operation(summary = "프로필 이미지 업로드", description = "JPG/PNG 이미지를 업로드하고 URL을 반환합니다. JWT 필요.")
    @PostMapping("/profile-image")
    public ApiResponse<Map<String, String>> uploadProfileImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        validateImageFile(file);

        Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath();
        Files.createDirectories(uploadPath);

        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        Path filePath = uploadPath.resolve(filename);
        file.transferTo(filePath.toFile());

        String url = backendUrl + "/uploads/profile-images/" + filename;
        return ApiResponse.ok(Map.of("url", url));
    }

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("파일이 비어있습니다.");
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하여야 합니다.");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}
