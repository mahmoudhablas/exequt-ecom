package com.exequt.ecom.model.entity;

import com.exequt.ecom.exception.CartNotActiveException;
import com.exequt.ecom.exception.CartNotBelongCustomerException;
import com.exequt.ecom.exception.EmptyCartCheckoutException;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "carts",
        indexes = {
                @Index(name = "idx_carts_customer_id", columnList = "customer_id"),
                @Index(name = "idx_carts_status", columnList = "status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CartStatus status = CartStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItemEntity> items;

    @Version
    private Long version;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void assertOwnership(Long userId) {
        if (!this.customer.getId().equals(userId)) {
            throw new CartNotBelongCustomerException(this.id, userId);
        }
    }

    public void assertActive() {
        if (this.status != CartStatus.ACTIVE) {
            throw new CartNotActiveException(this.id);
        }
    }
    public void checkout() {
        assertActive();
        if (this.items.isEmpty()) {
            throw new EmptyCartCheckoutException(this.id);
        }
        this.status = CartStatus.CHECKED_OUT;
        this.updatedAt = LocalDateTime.now();
    }
}