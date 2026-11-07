package com.emras.user.model;

import com.emras.shared.model.AuditModel;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends AuditModel {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String label = "Home";

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false, length = 255)
    private String street;

    @Column(length = 100)
    private String area;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 100)
    private String district;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(name = "is_default", nullable = false)
    @Builder.Default
    private boolean isDefault = false;
}