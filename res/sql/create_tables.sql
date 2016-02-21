CREATE TABLE user (
  id          INT PRIMARY KEY,
  name        VARCHAR
);

CREATE TABLE bookie (
  id          INT PRIMARY KEY,
  name        VARCHAR
);

CREATE TABLE account (
  user_id     INT,
  bookie_id   INT,
  FOREIGN KEY (user_id) REFERENCES user(id),
  FOREIGN KEY (bookie_id) REFERENCES bookie(id),
  PRIMARY KEY (user_id, bookie_id)
);

CREATE TABLE account_transaction (
  id          INT PRIMARY KEY,
  account_id  INT REFERENCES account,
  amount      INT
);
