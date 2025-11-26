package com.nttdata.user_service.domain.model;

import jakarta.persistence.*; // Spring Boot 3 usa jakarta, no javax
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<Phone> phones;

    private LocalDateTime created;
    private LocalDateTime modified;
    private LocalDateTime lastLogin;

    private String token;
    private boolean isActive;

    @PrePersist
    protected void onCreate() {
        this.created = LocalDateTime.now();
        this.lastLogin = this.created;
        this.isActive = true;
    }

    @PreUpdate
    protected void onUpdate() {
        this.modified = LocalDateTime.now();
    }

    public boolean validateTokenOwnership(String incomingToken) {
        return this.token != null && this.token.equals(incomingToken);
    }
}