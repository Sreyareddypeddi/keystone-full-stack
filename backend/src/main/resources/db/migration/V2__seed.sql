INSERT INTO customers(name,email,phone) VALUES
('Apex Commercial Towers','ops@apex.local','9000000001'),
('Nova Business Park','facilities@nova.local','9000000002');

INSERT INTO sites(customer_id,name,address,city,contact_name,contact_phone) VALUES
(1,'Apex Tower A','100 Business Road','Hyderabad','Ravi Kumar','9000000011'),
(1,'Apex Tower B','200 Business Road','Hyderabad','Anita Rao','9000000012'),
(2,'Nova Central','55 Tech Avenue','Hyderabad','Vikram Shah','9000000021');

-- BCrypt hash for the demo password: password
INSERT INTO users(customer_id,name,email,password_hash,role) VALUES
(NULL,'System Manager','manager@keystone.local','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','MANAGER'),
(NULL,'Main Dispatcher','dispatcher@keystone.local','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','DISPATCHER'),
(NULL,'Field Technician','technician@keystone.local','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','TECHNICIAN'),
(1,'Apex Customer','customer@keystone.local','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy','CUSTOMER');

INSERT INTO parts(sku,name,unit_cost,stock_quantity) VALUES
('HVAC-FLTR-01','HVAC Air Filter',18.50,50),
('ELEC-BRK-20','20A Circuit Breaker',12.75,30),
('PLMB-VAL-01','Copper Isolation Valve',24.90,20);

INSERT INTO work_orders(code,title,description,priority,status,customer_id,site_id,technician_id,sla_due_at)
VALUES
('WO-10001','HVAC cooling issue','Office floor is not cooling properly.','HIGH','NEW',1,1,NULL,CURRENT_TIMESTAMP + INTERVAL '4 hours'),
('WO-10002','Electrical inspection','Breaker trips intermittently.','MEDIUM','ASSIGNED',1,2,3,CURRENT_TIMESTAMP + INTERVAL '12 hours'),
('WO-10003','Leaking valve','Replace leaking isolation valve.','LOW','IN_PROGRESS',2,3,3,CURRENT_TIMESTAMP + INTERVAL '24 hours');

INSERT INTO work_order_status_history(work_order_id,from_status,to_status,changed_by,note)
VALUES
(1,NULL,'NEW',1,'Request created'),
(2,NULL,'NEW',2,'Request created'),
(2,'NEW','ASSIGNED',2,'Assigned to field technician'),
(3,NULL,'NEW',2,'Request created'),
(3,'NEW','ASSIGNED',2,'Assigned to field technician'),
(3,'ASSIGNED','IN_PROGRESS',3,'Technician started work');
