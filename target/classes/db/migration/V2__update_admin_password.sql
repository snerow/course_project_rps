-- Обновление пароля администратора (admin123)
-- Правильный BCrypt хеш для пароля "admin123"
UPDATE users 
SET password = '$2a$10$oAiOYF2CH4//8k/4GsDqZ.sG815LfH589ztUeK0znxM7UfScTXvUW'
WHERE username = 'admin';

