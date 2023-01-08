package pl.isekai.chat.controller.v1.response;

import java.util.UUID;

public record LoginResponse(
        String username,
        UUID key,
        String publicKey
) {
}
