package pl.isekai.chat.controller.v1.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.isekai.chat.controller.v1.request.AddMessageRequest;
import pl.isekai.chat.controller.v1.request.LoginRequest;
import pl.isekai.chat.controller.v1.response.LoginResponse;
import pl.isekai.chat.model.Message;
import pl.isekai.chat.model.User;
import pl.isekai.chat.service.ChatService;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1")
public record ChatController(
        ChatService chatService
) {
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> addUser(@RequestBody LoginRequest request) {
        User user = chatService.addUser(request.username(), request.publicKey());

        return ResponseEntity.ok(new LoginResponse(user.getUsername(), user.getId(), user.getPublicKey()));
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> addMessage(
            @RequestBody AddMessageRequest request,
            @RequestHeader("Key") UUID userId
    ) {
        return ResponseEntity.ok(chatService.addMessage(request.message(), userId));
    }

    @GetMapping("/messages")
    public ResponseEntity<Page<Message>> getMessages(
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader("Key") UUID userId) {
        return ResponseEntity.ok(chatService.getMessages(pageable, userId));
    }
}
