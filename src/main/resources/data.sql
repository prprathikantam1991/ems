-- Data initialization script for JPA demonstration
-- This script demonstrates various JPA features with sample data

-- Insert Roles
INSERT INTO roles (name, description)
VALUES
    ('ADMIN', 'Administrator with full access to all resources'),
    ('HR', 'Human Resources - can manage employees'),
    ('MANAGER', 'Department Manager - can view department employees'),
    ('USER', 'Regular user - basic access')
ON CONFLICT DO NOTHING;

-- Insert Users (Update google_id and email with your actual Google user info from JWT)
-- To get your Google ID: Decode your JWT token and look for the "sub" claim
-- Example users - REPLACE WITH YOUR ACTUAL GOOGLE USER DATA
INSERT INTO users (google_id, email, name, picture, created_at, updated_at, version)
VALUES
    -- Admin user (replace with your Google account info)
    ('112914472796745650281', 'pradeeprp1991@gmail.com', 'pradeep raju prathikantam', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    -- HR user (example - replace with actual user)
    ('HR_GOOGLE_ID_HERE', 'alice.williams@company.com', 'Alice Williams', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    -- Manager user (example - replace with actual user)
    ('MANAGER_GOOGLE_ID_HERE', 'diana.prince@company.com', 'Diana Prince', NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
ON CONFLICT DO NOTHING;

-- Assign Roles to Users
-- Admin role for first user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'pradeeprp1991@gmail.com' AND r.name = 'ADMIN'
ON CONFLICT DO NOTHING;

-- HR role for HR user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'alice.williams@company.com' AND r.name = 'HR'
ON CONFLICT DO NOTHING;

-- MANAGER role for manager user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'diana.prince@company.com' AND r.name = 'MANAGER'
ON CONFLICT DO NOTHING;

-- Note: New users logging in for the first time will automatically get USER role

-- Insert Departments
INSERT INTO departments (name, description, location, budget, head_count, created_at, updated_at, version)
VALUES
    ('Engineering', 'Software development and engineering', 'Building A, Floor 3', 5000000.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Human Resources', 'HR and talent management', 'Building A, Floor 2', 2000000.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Sales', 'Sales and business development', 'Building B, Floor 1', 3000000.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Marketing', 'Marketing and communications', 'Building B, Floor 2', 1500000.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Finance', 'Finance and accounting', 'Building A, Floor 1', 2500000.00, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
ON CONFLICT DO NOTHING;

-- Insert Employees
INSERT INTO employees (name, email, employee_id, status, phonenumber, address, hire_date, salary, job_title, department_id, created_at, updated_at, version)
VALUES
    ('John Doe', 'john.doe@company.com', 'EMP001', 'ACTIVE', '555-0101', '123 Main St, City', '2020-01-15', 75000.00, 'Senior Software Engineer', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Jane Smith', 'jane.smith@company.com', 'EMP002', 'ACTIVE', '555-0102', '456 Oak Ave, City', '2020-03-20', 80000.00, 'Lead Software Engineer', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Bob Johnson', 'bob.johnson@company.com', 'EMP003', 'ACTIVE', '555-0103', '789 Pine Rd, City', '2019-06-10', 65000.00, 'Software Engineer', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Alice Williams', 'alice.williams@company.com', 'EMP004', 'ACTIVE', '555-0104', '321 Elm St, City', '2021-02-01', 70000.00, 'HR Manager', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Charlie Brown', 'charlie.brown@company.com', 'EMP005', 'ACTIVE', '555-0105', '654 Maple Dr, City', '2020-08-15', 60000.00, 'HR Specialist', 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Diana Prince', 'diana.prince@company.com', 'EMP006', 'ACTIVE', '555-0106', '987 Cedar Ln, City', '2019-11-05', 90000.00, 'Sales Director', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Edward Norton', 'edward.norton@company.com', 'EMP007', 'ACTIVE', '555-0107', '147 Birch Way, City', '2021-04-12', 55000.00, 'Sales Representative', 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Fiona Green', 'fiona.green@company.com', 'EMP008', 'ACTIVE', '555-0108', '258 Spruce Ct, City', '2020-09-20', 65000.00, 'Marketing Manager', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('George White', 'george.white@company.com', 'EMP009', 'ACTIVE', '555-0109', '369 Willow Pl, City', '2021-01-08', 58000.00, 'Marketing Specialist', 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Helen Black', 'helen.black@company.com', 'EMP010', 'ACTIVE', '555-0110', '741 Ash Blvd, City', '2018-05-15', 85000.00, 'Finance Manager', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Ian Gray', 'ian.gray@company.com', 'EMP011', 'INACTIVE', '555-0111', '852 Poplar St, City', '2019-03-10', 62000.00, 'Accountant', 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0),
    ('Julia Red', 'julia.red@company.com', 'EMP012', 'ACTIVE', '555-0112', '963 Fir Ave, City', '2022-01-15', 72000.00, 'Software Engineer', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0)
ON CONFLICT DO NOTHING;


