package com.groupware.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "BLOCK")
public class BlockDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "BLOCKER_ID", nullable = false)
    private String blockerId;

    @Column(name = "BLOCKED_ID", nullable = false)
    private String blockedId;

    @Column(name = "CREATED_AT", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    // getter, setter 직접 추가하기

    public Long getId() {
        return id;
    }

    public String getBlockerId() {
        return blockerId;
    }

    public void setBlockerId(String blockerId) {
        this.blockerId = blockerId;
    }

    public String getBlockedId() {
        return blockedId;
    }

    public void setBlockedId(String blockedId) {
        this.blockedId = blockedId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
