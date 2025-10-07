-- สำหรับสร้าง table book
CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    published_date DATE
);

-- สำหรับทำ index ด้วย author
CREATE INDEX idx_book_author ON books(author);

