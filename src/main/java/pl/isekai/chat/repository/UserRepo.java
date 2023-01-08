package pl.isekai.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.isekai.chat.model.User;

import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {
    boolean existsByUsername(String username);
}
