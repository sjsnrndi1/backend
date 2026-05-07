package backend.repository;

import backend.User;
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
public class UserRepository {

    private static final Path DATA_FILE = Path.of("data", "users.json");
    private static final TypeReference<List<User>> USER_LIST_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public UserRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public synchronized Optional<User> findById(Long id) {
        return readUsers().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public synchronized Optional<User> findByEmail(String email) {
        String normalizedEmail = normalizeEmail(email);

        return readUsers().stream()
                .filter(user -> user.getEmail().equals(normalizedEmail))
                .findFirst();
    }

    public synchronized User save(User user) {
        List<User> users = readUsers();

        if (user.getId() == null) {
            user.setId(nextId(users));
            user.setCreatedAt(LocalDateTime.now());
            users.add(user);
        } else {
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getId().equals(user.getId())) {
                    users.set(i, user);
                    break;
                }
            }
        }

        writeUsers(users);
        return user;
    }

    private List<User> readUsers() {
        try {
            ensureDataFile();
            return objectMapper.readValue(DATA_FILE.toFile(), USER_LIST_TYPE);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read users JSON data file.", e);
        }
    }

    private void writeUsers(List<User> users) {
        try {
            ensureDataFile();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(DATA_FILE.toFile(), users);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write users JSON data file.", e);
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

    private Long nextId(List<User> users) {
        return users.stream()
                .map(User::getId)
                .filter(id -> id != null)
                .max(Comparator.naturalOrder())
                .map(id -> id + 1)
                .orElse(1L);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
