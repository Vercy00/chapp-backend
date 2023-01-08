package pl.isekai.chat.controller.v1.request;

import java.util.UUID;

public record AddMessageRequest(
        String message,
        UUID key
) {
}
