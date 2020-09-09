DROP TABLE IF EXISTS book;

CREATE TABLE book  (
    id INT GENERATED BY DEFAULT AS IDENTITY NOT NULL PRIMARY KEY,
    title VARCHAR(90),
    author VARCHAR(40),
    isbn VARCHAR(13),
    publisher VARCHAR(20),
    year INTEGER
);