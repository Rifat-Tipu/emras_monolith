package com.emras.user.model;
import com.emras.shared.model.AuditModel;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends AuditModel {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    /**
     * SHA-256 hash of the raw token.
     * We never store the raw token — only its hash.
     * The raw token lives only in the httpOnly cookie on the client.
     */
    @Column(name = "token_hash", nullable = false, unique = true, length = 64)
    private String tokenHash;
    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}