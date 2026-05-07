package backend;

import java.time.LocalDateTime;

public class Post {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdAt;

    private String attachmentUrl;

    private String attachmentOriginalName;

    private String attachmentStoredName;

    public Post() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getAttachmentOriginalName() {
        return attachmentOriginalName;
    }

    public void setAttachmentOriginalName(String attachmentOriginalName) {
        this.attachmentOriginalName = attachmentOriginalName;
    }

    public String getAttachmentStoredName() {
        return attachmentStoredName;
    }

    public void setAttachmentStoredName(String attachmentStoredName) {
        this.attachmentStoredName = attachmentStoredName;
    }
}
