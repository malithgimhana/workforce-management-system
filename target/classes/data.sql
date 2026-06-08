-- FlexiWork Seed Data (H2 compatible)
-- BCrypt hash for "password123": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- Companies
INSERT INTO companies (company_id, name, br_number, phone, email, address, balance, password, is_deleted, created_at)
VALUES
(1, 'ABC Garments Ltd', 'BR001234', '0112345678', 'info@abcgarments.lk', 'No 12, Industrial Zone, Katunayake', 0.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP),
(2, 'Sri Lanka Foods Pvt Ltd', 'BR005678', '0113456789', 'hr@slfoods.lk', 'No 45, Peliyagoda', 0.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP),
(3, 'Colombo Marketing Co', 'BR009012', '0114567890', 'admin@colombomarketing.lk', 'No 78, Maradana, Colombo 10', 0.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP),
(4, 'Kandy Restaurants Group', 'BR003456', '0812345678', 'ops@kandyrestaurants.lk', 'No 33, Kandy Road, Peradeniya', 0.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP),
(5, 'Galle IT Solutions', 'BR007890', '0912345678', 'contact@galleit.lk', 'No 5, Galle Fort, Galle', 0.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP);

-- Workers
INSERT INTO users (user_id, name, nic, phone, email, gender, address, password, is_deleted, created_at, updated_at)
VALUES
(1,  'Kamal Perera',           '199001234567', '0771234567', 'kamal@gmail.com',    'MALE',   'No 10, Gampaha',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2,  'Nimali Silva',           '199512345678', '0772345678', 'nimali@gmail.com',   'FEMALE', 'No 22, Negombo',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3,  'Rajan Kumar',            '198903456789', '0773456789', 'rajan@gmail.com',    'MALE',   'No 5, Jaffna',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4,  'Priya Fernando',         '200104567890', '0774567890', 'priya@gmail.com',    'FEMALE', 'No 8, Kandy',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5,  'Dinesh Wickrama',        '199705678901', '0775678901', 'dinesh@gmail.com',   'MALE',   'No 15, Matara',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6,  'Chamila Bandara',        '199806789012', '0776789012', 'chamila@gmail.com',  'FEMALE', 'No 3, Kurunegala',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7,  'Suresh Nair',            '199007890123', '0777890123', 'suresh@gmail.com',   'MALE',   'No 20, Colombo 6',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8,  'Ayesha Hameed',          '200208901234', '0778901234', 'ayesha@gmail.com',   'FEMALE', 'No 12, Puttalam',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9,  'Ruwan Jayasekara',       '199109012345', '0779012345', 'ruwan@gmail.com',    'MALE',   'No 7, Ratnapura',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Thilini Madhavi',        '199910123456', '0770123456', 'thilini@gmail.com',  'FEMALE', 'No 9, Badulla',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 'Amara Gunawardena',      '199211234567', '0761234567', 'amara@gmail.com',    'MALE',   'No 4, Anuradhapura',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'Sanduni Rathnayake',     '200312345678', '0762345678', 'sanduni@gmail.com',  'FEMALE', 'No 11, Polonnaruwa',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'Lahiru Pathirana',       '199513456789', '0763456789', 'lahiru@gmail.com',   'MALE',   'No 6, Hambantota',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'Nadeesha Kumari',        '199714567890', '0764567890', 'nadeesha@gmail.com', 'FEMALE', 'No 18, Trincomalee',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'Chathura Seneviratne',   '199015678901', '0765678901', 'chathura@gmail.com', 'MALE',   'No 2, Ampara',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(16, 'Malsha Wijesinghe',      '200116789012', '0766789012', 'malsha@gmail.com',   'FEMALE', 'No 25, Kegalle',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 'Isuru Dissanayake',      '199317890123', '0767890123', 'isuru@gmail.com',    'MALE',   'No 14, Nuwara Eliya', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'Hiruni Samarasinghe',    '199618901234', '0768901234', 'hiruni@gmail.com',   'FEMALE', 'No 30, Matale',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 'Nuwan Rajapaksha',       '199119012345', '0769012345', 'nuwan@gmail.com',    'MALE',   'No 1, Batticaloa',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 'Sachini Herath',         '200020123456', '0760123456', 'sachini@gmail.com',  'FEMALE', 'No 16, Vavuniya',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Company Users
INSERT INTO company_users (company_user_id, company_id, name, email, password, role)
VALUES
(1, 1, 'ABC IT Admin',      'itadmin@abcgarments.lk',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'IT_ADMIN'),
(2, 2, 'SLF IT Admin',      'itadmin@slfoods.lk',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'IT_ADMIN'),
(3, 3, 'CMC IT Admin',      'itadmin@colombomarketing.lk', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'IT_ADMIN'),
(4, 4, 'KRG HR Manager',    'hr@kandyrestaurants.lk',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'HR_MANAGER'),
(5, 5, 'GIT Factory Mgr',   'factory@galleit.lk',          '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'FACTORY_MANAGER');

-- Jobs
INSERT INTO jobs (job_id, company_id, title, description, daily_wage, shift_start_time, shift_end_time, required_workers, approved_workers, factory_location, latitude, longitude, gender, min_age, max_age, category, is_active, is_deleted, shift_date, created_at, updated_at)
VALUES
(1,  1, 'Garment Factory Worker',    'Sewing machine operator needed for export garments', 1800.00, '08:00:00', '17:00:00', 10, 3, 'Katunayake Industrial Zone, Katunayake', 7.1697, 79.8888, 'FEMALE', 18, 40, 'FACTORY',    true,  false, DATEADD('DAY', 2, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2,  1, 'Quality Control Inspector', 'Inspect finished garments for quality standards',     2200.00, '07:00:00', '15:00:00',  5, 2, 'Katunayake Industrial Zone, Katunayake', 7.1697, 79.8888, 'ANY',    21, 45, 'FACTORY',    true,  false, DATEADD('DAY', 3, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3,  2, 'Food Processing Worker',    'Pack and label food products on production line',     1600.00, '06:00:00', '14:00:00', 15, 5, 'Peliyagoda Industrial Area, Peliyagoda',  6.9601, 79.8847, 'ANY',    18, 50, 'FACTORY',    true,  false, DATEADD('DAY', 1, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4,  2, 'Restaurant Kitchen Helper', 'Assist chefs in preparation and cleaning',            1400.00, '10:00:00', '22:00:00',  8, 1, 'Colombo 3, Colombo',                      6.8921, 79.8520, 'MALE',   20, 35, 'RESTAURANT', true,  false, DATEADD('DAY', 2, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5,  3, 'Marketing Promoter',        'Distribute flyers and promote products in supermarkets', 1500.00, '09:00:00', '17:00:00', 20, 8, 'Colombo City, Colombo',              6.9271, 79.8612, 'ANY',    18, 30, 'MARKETING',  true,  false, DATEADD('DAY', 4, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(6,  3, 'Brand Ambassador',          'Represent brand at trade events',                      2500.00, '08:00:00', '18:00:00',  6, 2, 'BMICH, Colombo 7',                        6.9106, 79.8644, 'FEMALE', 20, 35, 'MARKETING',  true,  false, DATEADD('DAY', 5, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7,  4, 'Restaurant Waiter',         'Serve customers at Kandy branch',                     1300.00, '11:00:00', '23:00:00', 12, 4, 'Kandy City, Kandy',                       7.2906, 80.6337, 'ANY',    18, 40, 'RESTAURANT', true,  false, DATEADD('DAY', 1, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8,  4, 'Kitchen Cleaner',           'Clean and maintain kitchen equipment after service',  1200.00, '22:00:00', '06:00:00',  4, 0, 'Kandy City, Kandy',                       7.2906, 80.6337, 'MALE',   20, 45, 'RESTAURANT', true,  false, DATEADD('DAY', 3, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9,  5, 'IT Support Technician',     'On-site IT support during product launch event',      3000.00, '08:00:00', '20:00:00',  3, 1, 'Galle Fort, Galle',                       6.0353, 80.2170, 'ANY',    22, 40, 'OTHER',      true,  false, DATEADD('DAY', 7, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 1, 'Warehouse Loader',          'Load and unload finished goods at warehouse',         1700.00, '05:00:00', '13:00:00',  8, 2, 'Katunayake Export Zone, Katunayake',      7.1697, 79.8888, 'MALE',   20, 45, 'FACTORY',    false, false, DATEADD('DAY', -1, CURRENT_DATE), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Applications
INSERT INTO applications (application_id, user_id, job_id, status, applied_at, updated_at)
VALUES
(1,  1,  1, 'APPROVED', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_TIMESTAMP)),
(2,  2,  1, 'APPROVED', DATEADD('DAY', -5, CURRENT_TIMESTAMP), DATEADD('DAY', -4, CURRENT_TIMESTAMP)),
(3,  3,  1, 'PENDING',  DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP)),
(4,  4,  2, 'APPROVED', DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP)),
(5,  5,  2, 'APPROVED', DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP)),
(6,  6,  3, 'PENDING',  DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
(7,  7,  4, 'APPROVED', DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
(8,  8,  5, 'PENDING',  DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
(9,  9,  7, 'APPROVED', DATEADD('DAY', -4, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP)),
(10, 10, 9, 'APPROVED', DATEADD('DAY', -6, CURRENT_TIMESTAMP), DATEADD('DAY', -5, CURRENT_TIMESTAMP));

-- QR Verifications (past completed shifts)
INSERT INTO qr_verifications (verification_id, user_id, job_id, qr_token, scan_type, check_in_time, check_out_time, is_verified)
VALUES
(1, 1, 10, 'token-001-kamal-job10',  'CHECK_OUT', DATEADD('HOUR', -33, CURRENT_TIMESTAMP), DATEADD('HOUR', -25, CURRENT_TIMESTAMP), true),
(2, 2, 10, 'token-002-nimali-job10', 'CHECK_OUT', DATEADD('HOUR', -33, CURRENT_TIMESTAMP), DATEADD('HOUR', -25, CURRENT_TIMESTAMP), true),
(3, 7,  4, 'token-003-suresh-job4',  'CHECK_IN',  DATEADD('HOUR', -58, CURRENT_TIMESTAMP), NULL,                                    true),
(4, 9,  7, 'token-004-ruwan-job7',   'CHECK_OUT', DATEADD('HOUR', -85, CURRENT_TIMESTAMP), DATEADD('HOUR', -73, CURRENT_TIMESTAMP), true);

-- Commission Payments
INSERT INTO commission_payments (payment_id, company_id, job_id, worker_wage, commission_amount, status, paid_at, created_at)
VALUES
(1, 1, 10, 1700.00, 170.00, 'PAID',    DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
(2, 1, 10, 1700.00, 170.00, 'PAID',    DATEADD('DAY', -1, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
(3, 2,  4, 1400.00, 140.00, 'PENDING', NULL,                                   DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
(4, 4,  7, 1300.00, 130.00, 'PAID',    DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP));
