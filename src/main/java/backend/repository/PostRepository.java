package backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import backend.Post;

public interface PostRepository extends JpaRepository<Post, Long> {
}