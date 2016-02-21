CREATE TABLE user (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  name        VARCHAR
);

CREATE TABLE bookie (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  name        VARCHAR
);

CREATE TABLE account (
  user_id     INTEGER,
  bookie_id   INTEGER,
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (bookie_id) REFERENCES bookie(id),
  PRIMARY KEY (user_id, bookie_id)
);

CREATE TABLE account_transaction (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  account_id  INTEGER REFERENCES account,
  amount      INTEGER
);
