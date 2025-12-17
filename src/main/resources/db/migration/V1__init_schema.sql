-- Создание базы данных QualityDB
-- Система контроля качества пивоварни
--
-- ВАЖНО: База данных должна быть создана с кодировкой UTF-8
-- Выполните перед запуском скрипта:
-- CREATE DATABASE qualitydb WITH ENCODING 'UTF8' LC_COLLATE='ru_RU.UTF-8' LC_CTYPE='ru_RU.UTF-8' TEMPLATE template0;

-- Таблица ролей пользователей
CREATE TABLE IF NOT EXISTS roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    role_id INTEGER NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
);

-- Таблица производственных этапов
CREATE TABLE IF NOT EXISTS production_stages (
    stage_id SERIAL PRIMARY KEY,
    stage_name VARCHAR(100) NOT NULL UNIQUE,
    norms_ph_min DOUBLE PRECISION,
    norms_ph_max DOUBLE PRECISION,
    norms_density_min DOUBLE PRECISION,
    norms_density_max DOUBLE PRECISION,
    norms_alcohol_min DOUBLE PRECISION,
    norms_alcohol_max DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Таблица партий продукции
CREATE TABLE IF NOT EXISTS batches (
    batch_id SERIAL PRIMARY KEY,
    batch_number VARCHAR(50) NOT NULL UNIQUE,
    product_type VARCHAR(100) NOT NULL,
    status VARCHAR(50) DEFAULT 'CREATED',
    created_by INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Таблица проб
CREATE TABLE IF NOT EXISTS samples (
    sample_id SERIAL PRIMARY KEY,
    batch_id INTEGER NOT NULL,
    stage_id INTEGER NOT NULL,
    sampling_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(50) DEFAULT 'REGISTERED',
    collected_by INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id) ON DELETE CASCADE,
    FOREIGN KEY (stage_id) REFERENCES production_stages(stage_id) ON DELETE RESTRICT,
    FOREIGN KEY (collected_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Таблица анализов
CREATE TABLE IF NOT EXISTS analyses (
    analysis_id SERIAL PRIMARY KEY,
    sample_id INTEGER NOT NULL,
    analysis_type VARCHAR(50) NOT NULL, -- 'CHEMICAL' или 'MICROBIOLOGICAL'
    ph DOUBLE PRECISION,
    density DOUBLE PRECISION,
    alcohol_content DOUBLE PRECISION,
    within_norms BOOLEAN DEFAULT FALSE,
    performed_by INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sample_id) REFERENCES samples(sample_id) ON DELETE CASCADE,
    FOREIGN KEY (performed_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Таблица решений по качеству
CREATE TABLE IF NOT EXISTS quality_decisions (
    decision_id SERIAL PRIMARY KEY,
    batch_id INTEGER NOT NULL,
    decision VARCHAR(50) NOT NULL, -- 'APPROVED' или 'REJECTED'
    comment TEXT,
    approved BOOLEAN DEFAULT FALSE,
    decided_by INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id) ON DELETE CASCADE,
    FOREIGN KEY (decided_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Таблица журнала качества
CREATE TABLE IF NOT EXISTS quality_logs (
    log_id SERIAL PRIMARY KEY,
    batch_id INTEGER,
    operation_type VARCHAR(100) NOT NULL,
    operation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    message TEXT,
    user_id INTEGER,
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Таблица предупреждений об отклонениях
CREATE TABLE IF NOT EXISTS deviation_alerts (
    alert_id SERIAL PRIMARY KEY,
    sample_id INTEGER NOT NULL,
    analysis_id INTEGER NOT NULL,
    parameter_name VARCHAR(100) NOT NULL,
    actual_value DOUBLE PRECISION NOT NULL,
    norm_min DOUBLE PRECISION,
    norm_max DOUBLE PRECISION,
    message TEXT,
    notified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sample_id) REFERENCES samples(sample_id) ON DELETE CASCADE,
    FOREIGN KEY (analysis_id) REFERENCES analyses(analysis_id) ON DELETE CASCADE
);

-- Индексы для оптимизации запросов
CREATE INDEX idx_samples_batch_id ON samples(batch_id);
CREATE INDEX idx_samples_stage_id ON samples(stage_id);
CREATE INDEX idx_samples_status ON samples(status);
CREATE INDEX idx_analyses_sample_id ON analyses(sample_id);
CREATE INDEX idx_analyses_within_norms ON analyses(within_norms);
CREATE INDEX idx_quality_logs_batch_id ON quality_logs(batch_id);
CREATE INDEX idx_quality_logs_operation_date ON quality_logs(operation_date);
CREATE INDEX idx_batches_status ON batches(status);
CREATE INDEX idx_batches_batch_number ON batches(batch_number);

-- Вставка начальных данных
INSERT INTO roles (name) VALUES 
    ('ADMIN'),
    ('LABORATORY_ASSISTANT'),
    ('TECHNOLOGIST')
ON CONFLICT (name) DO NOTHING;

-- Вставка производственных этапов
INSERT INTO production_stages (stage_name, norms_ph_min, norms_ph_max, norms_density_min, norms_density_max, norms_alcohol_min, norms_alcohol_max) VALUES
    ('Затирание', 5.2, 5.6, NULL, NULL, NULL, NULL),
    ('Фильтрация', 5.4, 5.8, NULL, NULL, NULL, NULL),
    ('Варка', 5.0, 5.5, NULL, NULL, NULL, NULL),
    ('Ферментация', 4.2, 4.6, 1.040, 1.060, NULL, NULL),
    ('Дображивание', 4.0, 4.5, 1.008, 1.015, 4.0, 6.0),
    ('Готовая продукция', 4.0, 4.5, 1.008, 1.015, 4.0, 6.0)
ON CONFLICT (stage_name) DO NOTHING;

-- Создание пользователя администратора по умолчанию (пароль: admin123)
INSERT INTO users (username, password, full_name, role_id) VALUES
    ('admin', '$2a$10$oAiOYF2CH4//8k/4GsDqZ.sG815LfH589ztUeK0znxM7UfScTXvUW', 'Генеральный директор', 1)
ON CONFLICT (username) DO NOTHING;

