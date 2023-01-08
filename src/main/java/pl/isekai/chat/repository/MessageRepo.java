package pl.isekai.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.isekai.chat.model.Message;

public interface MessageRepo extends JpaRepository<Message, Long> {
}
