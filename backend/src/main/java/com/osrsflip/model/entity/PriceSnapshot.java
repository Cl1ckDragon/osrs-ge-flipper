package com.osrsflip.model.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "price_snapshots", indexes = {
        @Index(name = "idx_price_snapshots_item_id", columnList = "item_id"),
        @Index(name = "idx_price_snapshots_fetched_at", columnList = "fetched_at DESC")
})
public class PriceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;

    @Column(nullable = false)
    private Integer high;

    @Column(nullable = false)
    private Integer low;

    @Column(name = "buy_limit", nullable = false)
    private Integer buyLimit;

    @Column(name = "flip_score", nullable = false)
    private Long flipScore;

    @Column(name = "fetched_at", nullable = false)
    private OffsetDateTime fetchedAt;

    public Long getId() { return id; }
    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public Integer getHigh() { return high; }
    public void setHigh(Integer high) { this.high = high; }
    public Integer getLow() { return low; }
    public void setLow(Integer low) { this.low = low; }
    public Integer getBuyLimit() { return buyLimit; }
    public void setBuyLimit(Integer buyLimit) { this.buyLimit = buyLimit; }
    public Long getFlipScore() { return flipScore; }
    public void setFlipScore(Long flipScore) { this.flipScore = flipScore; }
    public OffsetDateTime getFetchedAt() { return fetchedAt; }
    public void setFetchedAt(OffsetDateTime fetchedAt) { this.fetchedAt = fetchedAt; }
}
