package backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    FileUploadResult upload(MultipartFile file);
}