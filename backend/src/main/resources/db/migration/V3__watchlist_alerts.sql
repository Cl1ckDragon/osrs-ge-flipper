CREATE TABLE watchlist_items (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id     INTEGER      NOT NULL,
    item_name   VARCHAR(100) NOT NULL,
    icon        VARCHAR(200),
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, item_id)
);

CREATE TABLE price_alerts (
    id             BIGSERIAL PRIMARY KEY,
    user_id        BIGINT       NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    item_id        INTEGER      NOT NULL,
    item_name      VARCHAR(100) NOT NULL,
    icon           VARCHAR(200),
    target_margin  INTEGER      NOT NULL,
    triggered      BOOLEAN      NOT NULL DEFAULT FALSE,
    triggered_at   TIMESTAMPTZ,
    dismissed      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_price_alerts_user_triggered ON price_alerts (user_id, triggered, dismissed);
