package pl.isekai.chat.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message implements Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private String author;

    @Column(name = "offset_date_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime sentAt;

    @Override
    public Message clone() throws CloneNotSupportedException {
        return (Message) super.clone();
    }
}
