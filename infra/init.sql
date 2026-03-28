-- Roles
INSERT INTO role (id, role_name, description) VALUES
                                                  (1, 'ADMIN', 'Quản trị viên toàn quyền hệ thống'),
                                                  (2, 'MODERATOR', 'Người kiểm duyệt nội dung'),
                                                  (3, 'CREATOR', 'Người sáng tạo nội dung'),
                                                  (4, 'CONSUMER', 'Người mua nội dung');

-- Users
INSERT INTO users (id, username, email, password, full_name, phone_number, status, created_at, updated_at) VALUES
                                                                                                               (1, 'admin', 'admin@studyhard.com', '$2a$10$hashedpassword1', 'Super Admin', '0901000001', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                               (2, 'moderator', 'moderator@studyhard.com', '$2a$10$hashedpassword2', 'Mod One', '0901000002', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                               (3, 'consumer1', 'consumer1@studyhard.com', '$2a$10$hashedpassword3', 'Nguyen Van A', '0901000003', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                               (4, 'consumer2', 'consumer2@studyhard.com', '$2a$10$hashedpassword4', 'Tran Thi B', '0901000004', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                               (5, 'creator1', 'creator1@studyhard.com', '$2a$10$hashedpassword5', 'Le Van C', '0901000005', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- User Roles
INSERT INTO user_role (user_id, role_id, created_at, updated_at) VALUES
                                                                     (1, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     (2, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     (3, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     (4, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                     (5, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Categories
INSERT INTO category (id, name, slug, description, created_at, updated_at) VALUES
                                                                               (1, 'Java', 'java', 'Các khóa học về lập trình Java', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                               (2, 'Web Development', 'web-development', 'Phát triển Frontend và Backend', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                               (3, 'Data Science', 'data-science', 'Phân tích dữ liệu và Machine Learning', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                               (4, 'DevOps', 'devops', 'Triển khai và vận hành hệ thống', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                               (5, 'Mobile Development', 'mobile-development', 'Lập trình ứng dụng di động', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                               (6, 'Database', 'database', 'Cơ sở dữ liệu và tối ưu hóa', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Contents
INSERT INTO content (category_id, creator_id, title, description, status, level, price, view_count, purchase_count, published_at, created_at, updated_at) VALUES
                                                                                                                                                              (1, 5, 'Lập trình Java căn bản', 'Khóa học Java dành cho người mới bắt đầu từ con số 0.', 'PUBLISHED', 'BEGINNER', 499000.00, 150, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                              (2, 5, 'Fullstack Web với Next.js', 'Học cách xây dựng ứng dụng web hiện đại, chuẩn SEO.', 'PUBLISHED', 'ADVANCED', 1200000.00, 340, 42, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                              (3, 5, 'Phân tích dữ liệu với Python', 'Sử dụng Pandas, Numpy và Matplotlib để trực quan hóa dữ liệu.', 'DRAFT', 'INTERMEDIATE', 850000.00, 10, 0, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                              (4, 5, 'Triển khai hệ thống với Docker & K8s', 'Làm chủ quy trình CI/CD và containerization.', 'PUBLISHED', 'ADVANCED', 1500000.00, 89, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                              (5, 5, 'Lập trình Mobile với Flutter', 'Xây dựng ứng dụng đa nền tảng iOS và Android.', 'PUBLISHED', 'INTERMEDIATE', 600000.00, 210, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                                                                                                                                                              (6, 5, 'Tối ưu hóa MySQL cho hệ thống lớn', 'Học cách đánh Index, tối ưu Query và thiết kế Database chuẩn.', 'PUBLISHED', 'INTERMEDIATE', 750000.00, 45, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);