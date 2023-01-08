package pl.isekai.chat.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import pl.isekai.chat.service.ChatService;

@Component
public record WebSocketDisconnectListener(
        ChatService chatService
) implements ApplicationListener<SessionDisconnectEvent> {
    @Override
    @EventListener
    public void onApplicationEvent(SessionDisconnectEvent event) {
        chatService.deleteWebsocketUser(event.getSessionId());
    }
}
