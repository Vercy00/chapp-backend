package pl.isekai.chat.controller.v1.api;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import pl.isekai.chat.controller.v1.request.AddMessageRequest;
import pl.isekai.chat.model.Message;
import pl.isekai.chat.service.ChatService;

@Controller
public record WebSocketController(
        ChatService chatService,
        SimpMessagingTemplate simpMessagingTemplate
) {
    @MessageMapping("/chat")
    public void send(AddMessageRequest request,
                     @Header("simpSessionId") String sessionId) {
        chatService.addWebsocketUser(sessionId, request.key());

        Message message = chatService.addMessage(request.message(), request.key());

        if (message != null)
            chatService.sendMessage(message);
    }
}
