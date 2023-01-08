package pl.isekai.chat.controller.v1.request;

public record LoginRequest(
        String username,
        String publicKey
) {
}
