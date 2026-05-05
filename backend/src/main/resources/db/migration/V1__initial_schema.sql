CREATE TABLE price_snapshots (
    id          BIGSERIAL PRIMARY KEY,
    item_id     INTEGER NOT NULL,
    item_name   VARCHAR(100) NOT NULL,
    high        INTEGER NOT NULL,
    low         INTEGER NOT NULL,
    buy_limit   INTEGER NOT NULL DEFAULT 0,
    flip_score  BIGINT NOT NULL,
    fetched_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_price_snapshots_item_id   ON price_snapshots (item_id);
CREATE INDEX idx_price_snapshots_fetched_at ON price_snapshots (fetched_at DESC);
