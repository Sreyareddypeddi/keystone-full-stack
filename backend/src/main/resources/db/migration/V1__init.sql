CREATE TABLE customers (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(40),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sites (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    name VARCHAR(150) NOT NULL,
    address VARCHAR(300) NOT NULL,
    city VARCHAR(100) NOT NULL,
    contact_name VARCHAR(150),
    contact_phone VARCHAR(40)
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT REFERENCES customers(id),
    name VARCHAR(150) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE parts (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    unit_cost NUMERIC(12,2) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0 CHECK (stock_quantity >= 0)
);

CREATE TABLE work_orders (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    description VARCHAR(2000),
    priority VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL,
    customer_id BIGINT NOT NULL REFERENCES customers(id),
    site_id BIGINT NOT NULL REFERENCES sites(id),
    technician_id BIGINT REFERENCES users(id),
    sla_due_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT work_order_site_customer_fk CHECK (customer_id > 0 AND site_id > 0)
);

CREATE TABLE work_order_status_history (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL REFERENCES work_orders(id),
    from_status VARCHAR(30),
    to_status VARCHAR(30) NOT NULL,
    changed_by BIGINT NOT NULL REFERENCES users(id),
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(500)
);

CREATE TABLE part_usage (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL REFERENCES work_orders(id),
    part_id BIGINT NOT NULL REFERENCES parts(id),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    unit_cost NUMERIC(12,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE time_logs (
    id BIGSERIAL PRIMARY KEY,
    work_order_id BIGINT NOT NULL REFERENCES work_orders(id),
    technician_id BIGINT NOT NULL REFERENCES users(id),
    minutes INTEGER NOT NULL CHECK (minutes > 0),
    note VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    message VARCHAR(500) NOT NULL,
    read_flag BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wo_status ON work_orders(status);
CREATE INDEX idx_wo_customer ON work_orders(customer_id);
CREATE INDEX idx_wo_technician ON work_orders(technician_id);
CREATE INDEX idx_wo_sla ON work_orders(sla_due_at);
CREATE INDEX idx_history_work_order ON work_order_status_history(work_order_id);
