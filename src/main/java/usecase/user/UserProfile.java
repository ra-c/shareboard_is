package usecase.user;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Classe DTO relativa alla visualizzazione del profilo utente.
 */
@Data @Builder
public class UserProfile {
    private Integer id;
    private String username;
    private String email;
    private Instant creationDate;
    private String picture;
    private String description;
    private boolean isAdmin;
}
