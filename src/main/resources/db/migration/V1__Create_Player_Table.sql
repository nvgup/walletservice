CREATE TABLE IF NOT EXISTS player (
  id BIGINT NOT NULL,
  version LONG NOT NULL,
  balance DECIMAL(10, 2) NOT NULL default 0,
  PRIMARY KEY (id)
) ENGINE=InnoDB;