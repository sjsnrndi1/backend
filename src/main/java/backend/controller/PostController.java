package backend.controller;

import backend.Post;
import backend.repository.PostRepository;
import backend.service.FileStorageService;
import backend.service.FileUploadResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posts")
@CrossOrigin
public class PostController {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;

    public PostController(PostRepository postRepository,
                          FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public List<Post> getPosts() {
        return postRepository.findAll();
    }

    @PostMapping
    public Post createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);

        if (file != null && !file.isEmpty()) {
            FileUploadResult result = fileStorageService.upload(file);

            post.setAttachmentOriginalName(result.getOriginalFileName());
            post.setAttachmentStoredName(result.getStoredFileName());
            post.setAttachmentUrl(result.getFileUrl());
        }

        return postRepository.save(post);
    }

    @GetMapping("/{id}")
    public Post getPost(@PathVariable Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다. id=" + id));
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable Long id) {
        postRepository.deleteById(id);
    }
}