DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS author;

CREATE TABLE IF NOT EXISTS author
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL
    );

CREATE TABLE IF NOT EXISTS book
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(13) NOT NULL,
    published_date DATE NULL,
    title VARCHAR(255) NOT NULL,
    author_id BIGINT NULL,
    CONSTRAINT UK_isbn UNIQUE (isbn),
    CONSTRAINT FK_author FOREIGN KEY (author_id)
        REFERENCES author (id)
        ON DELETE CASCADE
);

CREATE INDEX published_date ON book(published_date);
