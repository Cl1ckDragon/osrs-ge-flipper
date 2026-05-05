package com.osrsflip.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "price_alerts")
public class PriceAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(length = 200)
    private String icon;

    @Column(name = "target_margin", nullable = false)
    private Integer targetMargin;

    @Column(nullable = false)
    private Boolean triggered = false;

    @Column(name = "triggered_at")
    private OffsetDateTime triggeredAt;

    @Column(nullable = false)
    private Boolean dismissed = false;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public Integer getTargetMargin() { return targetMargin; }
    public void setTargetMargin(Integer targetMargin) { this.targetMargin = targetMargin; }
    public Boolean getTriggered() { return triggered; }
    public void setTriggered(Boolean triggered) { this.triggered = triggered; }
    public OffsetDateTime getTriggeredAt() { return triggeredAt; }
    public void setTriggeredAt(OffsetDateTime triggeredAt) { this.triggeredAt = triggeredAt; }
    public Boolean getDismissed() { return dismissed; }
    public void setDismissed(Boolean dismissed) { this.dismissed = dismissed; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
