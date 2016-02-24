CREATE TABLE user (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  name        TEXT
);

CREATE TABLE bookie (
  id          INTEGER PRIMARY KEY AUTOINCREMENT,
  name        TEXT
);

CREATE TABLE account (
  user_id     INTEGER REFERENCES user(id),
  bookie_id   INTEGER REFERENCES bookie(id),
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
  outcome         TEXT,
  bookie_id       INTEGER REFERENCES bookie(id),
  transaction_id  INTEGER REFERENCES account_transaction(id),
  arbitration_id  INTEGER REFERENCES arbitration(id),
  PRIMARY KEY (transaction_id, arbitration_id)
);

CREATE TABLE sport (
  id              INTEGER PRIMARY KEY AUTOINCREMENT,
  title           TEXT
);

CREATE TABLE game (
  id              INTEGER PRIMARY KEY AUTOINCREMENT,
  sport_id        INTEGER REFERENCES sport(id)
);

CREATE TABLE game_outcome (
  id              INTEGER PRIMARY KEY AUTOINCREMENT,
  game_id         INTEGER REFERENCES game(id),
  outcome         TEXT
);

CREATE TABLE odd (
  id              INTEGER PRIMARY KEY AUTOINCREMENT,
  numerator       INTEGER,
  denominator     INTEGER,
  time            INTEGER,
  bookie_id       INTEGER REFERENCES bookie(id),
  game_id         INTEGER REFERENCES game(id),
  outcome_id      INTEGER REFERENCES game_outcome(id)
);
