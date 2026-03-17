CREATE TABLE items (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT 'Unique identifier',
    name        VARCHAR(255) NOT NULL COMMENT 'Item name',
    description TEXT COMMENT 'Item description',
    status      VARCHAR(20) NOT NULL COMMENT 'Item status: ACTIVE, INACTIVE, ARCHIVED',
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_items_status ON items (status);
CREATE INDEX idx_items_name ON items (name);
