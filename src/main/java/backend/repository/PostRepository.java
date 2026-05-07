package backend.repository;

import backend.Post;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Repository
public class PostRepository {

    private static final Path DATA_FILE = Path.of("data", "posts.json");
    private static final TypeReference<List<Post>> POST_LIST_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public PostRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public synchronized List<Post> findAll() {
        return readPosts();
    }

    public synchronized Optional<Post> findById(Long id) {
        return readPosts().stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }

    public synchronized Post save(Post post) {
        List<Post> posts = readPosts();

        if (post.getId() == null) {
            post.setId(nextId(posts));
            post.setCreatedAt(LocalDateTime.now());
            posts.add(post);
        } else {
            boolean updated = false;

            for (int i = 0; i < posts.size(); i++) {
                if (posts.get(i).getId().equals(post.getId())) {
                    posts.set(i, post);
                    updated = true;
                    break;
                }
            }

            if (!updated) {
                posts.add(post);
            }
        }

        writePosts(posts);
        return post;
    }

    public synchronized void deleteById(Long id) {
        List<Post> posts = readPosts();
        posts.removeIf(post -> post.getId().equals(id));
        writePosts(posts);
    }

    private List<Post> readPosts() {
        try {
            ensureDataFile();
            return objectMapper.readValue(DATA_FILE.toFile(), POST_LIST_TYPE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read JSON data file.", e);
        }
    }

    private void writePosts(List<Post> posts) {
        try {
            ensureDataFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(DATA_FILE.toFile(), posts);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write JSON data file.", e);
        }
    }

    private void ensureDataFile() throws IOException {
        Path parent = DATA_FILE.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        if (Files.notExists(DATA_FILE)) {
            Files.writeString(DATA_FILE, "[]");
        }
    }

    private Long nextId(List<Post> posts) {
        return posts.stream()
                .map(Post::getId)
                .filter(id -> id != null)
                .max(Comparator.naturalOrder())
                .map(id -> id + 1)
                .orElse(1L);
    }
}
