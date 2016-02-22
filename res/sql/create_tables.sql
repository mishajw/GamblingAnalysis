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

CREATE TABLE arbitration (
  id          INTEGER PRIMARY KEY AUTOINCREMENT
);

CREATE TABLE arbitration_transactions (
  transaction_id                INTEGER,
  arbitration_id                INTEGER,
  FOREIGN KEY (transaction_id)  REFERENCES account_transaction(id),
  FOREIGN KEY (arbitration_id)  REFERENCES arbitration(id),
  PRIMARY KEY (transaction_id, arbitration_id)
);
