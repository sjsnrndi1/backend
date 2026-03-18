package backend.service;

public class FileUploadResult {

    private String originalFileName;
    private String storedFileName;
    private String fileUrl;

    public FileUploadResult(String originalFileName, String storedFileName, String fileUrl) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileUrl = fileUrl;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}