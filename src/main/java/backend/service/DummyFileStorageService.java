package backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class DummyFileStorageService implements FileStorageService {

    @Override
    public FileUploadResult upload(MultipartFile file) {

        String originalName = file.getOriginalFilename();
        String storedName = UUID.randomUUID() + "_" + originalName;

        // 실제 업로드는 안 하고 가짜 URL 생성
        String fakeUrl = "https://dummy-storage/" + storedName;

        return new FileUploadResult(originalName, storedName, fakeUrl);
    }
}