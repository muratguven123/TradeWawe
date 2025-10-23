-- TradeWave - Users Tablosu Düzeltme Script'i
-- Bu script users tablosunu doğru ID generation stratejisi ile yeniden oluşturur

-- Önce mevcut tabloyu sil (dikkat: veriler silinecek!)
DROP TABLE IF EXISTS users CASCADE;

-- Users tablosunu doğru şekilde oluştur
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) CHECK (role IN ('USER', 'ADMIN', 'SELLER'))
);

-- Test için örnek kullanıcı ekle (opsiyonel)
-- INSERT INTO users (email, name, password, role)
-- VALUES ('test@example.com', 'Test User', '$2a$10$encrypted_password', 'USER');

-- Tabloyu kontrol et
SELECT * FROM users;

