package pl.isekai.chat.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.isekai.chat.model.Message;
import pl.isekai.chat.model.User;

import java.util.UUID;

public interface ChatService {
    User addUser(String username, String publicKeyStr);
    Message addMessage(String message, UUID userId);
    Page<Message> getMessages(Pageable pageable, UUID userId);
    void addWebsocketUser(String sessionId, UUID userId);
    void deleteWebsocketUser(String sessionId);
    void sendMessage(Message message);
}
