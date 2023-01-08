package pl.isekai.chat.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_user")
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    @Column(length = 4096)
    private String publicKey;

    @Column(length = 4096)
    private String privateKey;
}
