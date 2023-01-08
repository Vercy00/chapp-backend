package pl.isekai.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pl.isekai.chat.exception.ConflictException;
import pl.isekai.chat.exception.NotFoundException;
import pl.isekai.chat.model.Message;
import pl.isekai.chat.model.User;
import pl.isekai.chat.repository.MessageRepo;
import pl.isekai.chat.repository.UserRepo;
import pl.isekai.chat.util.CensorUtil;
import pl.isekai.chat.util.EncryptUtil;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final Map<String, UUID> websocketUsers = new HashMap<>();

    @Override
    public User addUser(String username, String publicKeyStr) {
        if (userRepo.existsByUsername(username))
            throw new ConflictException("User with same username already exists.");

        try {
            KeyPair keyPair = EncryptUtil.generateRSAKeyPair();

            User user = User.builder()
                    .publicKey(publicKeyStr)
                    .privateKey(EncryptUtil.keyToString(keyPair.getPrivate()))
                    .username(username)
                    .build();

            user = userRepo.save(user);
            userRepo.flush();

            return User.builder()
                    .id(user.getId())
                    .username(username)
                    .publicKey(EncryptUtil.keyToString(keyPair.getPublic()))
                    .build();
        } catch (Exception ignored) {
            throw new RuntimeException("Something went wrong.");
        }
    }

    @Override
    public Message addMessage(String messageStr, UUID userId) {
        try {
            User user = userRepo.findById(userId).orElseThrow(() ->
                    new NotFoundException("User not found."));
            PrivateKey privateKey = (PrivateKey) EncryptUtil.stringToKey(user.getPrivateKey(), PrivateKey.class);
            String decryptedMess = EncryptUtil.decryptString(messageStr, privateKey);
            decryptedMess = CensorUtil.filter(decryptedMess);

            if (decryptedMess.equals("!@#$%^&*())(*&^%$#@!"))
                return null;

            Message message = Message.builder()
                    .message(decryptedMess)
                    .author(user.getUsername())
                    .sentAt(OffsetDateTime.now())
                    .build();

            message = messageRepo.save(message);
            messageRepo.flush();

            return message;
        } catch (Exception ignored) {
            throw new RuntimeException("Something went wrong.");
        }
    }

    @Override
    public Page<Message> getMessages(Pageable pageable, UUID userId) {
        try {
            User user = userRepo.findById(userId).orElseThrow(() ->
                    new NotFoundException("User not found."));
            PublicKey publicKey = (PublicKey) EncryptUtil.stringToKey(user.getPublicKey(), PublicKey.class);
            Page<Message> messages = messageRepo.findAll(pageable);
            messages.getContent().forEach(message -> {
                try {
                    String encryptedMessage = EncryptUtil.encryptString(
                            message.getMessage(),
                            publicKey
                    );

                    message.setMessage(encryptedMessage);
                } catch (Exception ignored) {
                }
            });

            return messages;
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public void addWebsocketUser(String sessionId, UUID userId) {
        websocketUsers.put(sessionId, userId);
    }

    @Override
    public void deleteWebsocketUser(String sessionId) {
        websocketUsers.remove(sessionId);
    }

    @Override
    public void sendMessage(Message message) {
        websocketUsers.forEach((sessionId, userId) -> {
            try {
                User user = userRepo.findById(userId).orElseThrow(() ->
                        new NotFoundException("User not found."));
                Message tempMess = message.clone();
                PublicKey publicKey = (PublicKey) EncryptUtil.stringToKey(user.getPublicKey(), PublicKey.class);
                tempMess.setMessage(EncryptUtil.encryptString(
                        tempMess.getMessage(),
                        publicKey
                ));

                simpMessagingTemplate.convertAndSendToUser(userId.toString(), "/topic/messages", tempMess);
            } catch (Exception ignored)
            {
            }
        });
    }
}
