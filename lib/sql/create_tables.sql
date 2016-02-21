CREATE TABLE user (
  id INT PRIMARY KEY,
  name VARCHAR
);

CREATE TABLE bookie (
  id INT PRIMARY KEY,
  name VARCHAR
);

CREATE TABLE account (
  user_id    INT PRIMARY KEY,
  bookie_id  INT PRIMARY KEY,
  PRIMARY KEY (user_id, bookie_id)
);

CREATE TABLE account_transaction (
  id INT PRIMARY KEY,
  account_id INT REFERENCES account,
  amount INT
);
