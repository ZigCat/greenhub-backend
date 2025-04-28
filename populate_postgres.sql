-- Add 10 additional users with USER role
INSERT INTO users (first_name, last_name, email, password, role) VALUES
('John', 'Doe', 'john.doe@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Jane', 'Smith', 'jane.smith@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Michael', 'Johnson', 'michael.j@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Emily', 'Williams', 'emily.w@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('David', 'Brown', 'david.b@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Sarah', 'Miller', 'sarah.m@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('James', 'Wilson', 'james.w@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Emma', 'Taylor', 'emma.t@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Robert', 'Anderson', 'robert.a@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Linda', 'Thomas', 'linda.t@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER');

-- Add user.read scope for all regular users
INSERT INTO user_scopes (user_id, scopes)
SELECT user_id, 'user.read' FROM users WHERE role = 'USER';

-- Add article.read scope for all regular users
INSERT INTO user_scopes (user_id, scopes)
SELECT user_id, 'article.read' FROM users WHERE role = 'USER';

-- Insert categories
INSERT INTO categories (name, description) VALUES
('Technology', 'Articles about the latest tech trends and innovations'),
('Science', 'Scientific discoveries and research breakthroughs'),
('Business', 'Business strategies, entrepreneurship, and market trends'),
('Health', 'Health tips, medical research, and wellness advice'),
('Culture', 'Arts, entertainment, and cultural phenomena');

-- Insert 20 articles
INSERT INTO articles (title, creation_date, article_status, paid_status, creator_id, category_id, annotation) VALUES
-- Technology articles
('The Future of Quantum Computing', NOW() - INTERVAL '30 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'zigcat517@gmail.com'),
 (SELECT category_id FROM categories WHERE name = 'Technology'),
 'An overview of recent advances in quantum computing and its potential applications'),

('AI in Healthcare: Transforming Patient Care', NOW() - INTERVAL '28 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'lenin.denis@mail.ru'),
 (SELECT category_id FROM categories WHERE name = 'Technology'),
 'How artificial intelligence is revolutionizing healthcare delivery and patient outcomes'),

('Blockchain Beyond Cryptocurrency', NOW() - INTERVAL '25 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'kim635107@gmail.com'),
 (SELECT category_id FROM categories WHERE name = 'Technology'),
 'Exploring the various applications of blockchain technology outside of digital currencies'),

('The Rise of Edge Computing', NOW() - INTERVAL '21 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'john.doe@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Technology'),
 'Understanding edge computing and its advantages over traditional cloud computing'),

-- Science articles
('Breakthrough in Nuclear Fusion Research', NOW() - INTERVAL '19 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'jane.smith@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Science'),
 'Recent advancements bringing sustainable fusion energy closer to reality'),

('Dark Matter: The Hunt Continues', NOW() - INTERVAL '18 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'zigcat517@gmail.com'),
 (SELECT category_id FROM categories WHERE name = 'Science'),
 'Latest research and experiments in the ongoing search for dark matter'),

('CRISPR Gene Editing: Ethical Considerations', NOW() - INTERVAL '17 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'michael.j@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Science'),
 'Exploring the ethical implications of gene editing technology'),

('Climate Change: New Models and Predictions', NOW() - INTERVAL '15 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'kim635107@gmail.com'),
 (SELECT category_id FROM categories WHERE name = 'Science'),
 'Updated climate models revealing new insights about global warming trajectories'),

-- Business articles
('Remote Work Revolution: The New Normal', NOW() - INTERVAL '14 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'emily.w@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Business'),
 'How remote work is reshaping corporate culture and business operations'),

('Sustainable Business Practices That Boost Profit', NOW() - INTERVAL '13 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'lenin.denis@mail.ru'),
 (SELECT category_id FROM categories WHERE name = 'Business'),
 'Case studies of companies succeeding with environmentally sustainable business models'),

('The Gig Economy in 2025', NOW() - INTERVAL '12 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'david.b@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Business'),
 'Trends and predictions for freelance work and the gig economy'),

('Venture Capital Trends in Emerging Markets', NOW() - INTERVAL '11 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'sarah.m@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Business'),
 'Analysis of investment patterns in developing economies'),

-- Health articles
('Nutrition Myths Debunked', NOW() - INTERVAL '10 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'james.w@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Health'),
 'Scientific examination of common nutrition claims and misconceptions'),

('Mental Health in the Digital Age', NOW() - INTERVAL '9 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'zigcat517@gmail.com'),
 (SELECT category_id FROM categories WHERE name = 'Health'),
 'How technology affects psychological wellbeing and strategies for digital wellness'),

('Advances in Cancer Immunotherapy', NOW() - INTERVAL '8 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'emma.t@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Health'),
 'Recent breakthroughs in using the immune system to fight cancer'),

('Sleep Science: New Research and Insights', NOW() - INTERVAL '7 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'lenin.denis@mail.ru'),
 (SELECT category_id FROM categories WHERE name = 'Health'),
 'What recent studies reveal about sleep and its impact on health'),

-- Culture articles
('The Evolution of Virtual Reality in Art', NOW() - INTERVAL '6 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'robert.a@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Culture'),
 'How VR technology is transforming artistic expression and audience experiences'),

('Global Fusion Cuisine: Cultural Exchange Through Food', NOW() - INTERVAL '5 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'linda.t@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Culture'),
 'The rich history and current trends in cross-cultural culinary innovations'),

('The Resurgence of Independent Cinema', NOW() - INTERVAL '3 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'kim635107@gmail.com'),
 (SELECT category_id FROM categories WHERE name = 'Culture'),
 'How independent filmmakers are finding new audiences in the streaming era'),

('Traditional Crafts in the Modern Marketplace', NOW() - INTERVAL '1 day', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'zigcat517@gmail.com'),
 (SELECT category_id FROM categories WHERE name = 'Culture'),
 'Artisans adapting traditional techniques for contemporary consumers');

-- Add some articles in MODERATION status
INSERT INTO articles (title, creation_date, article_status, paid_status, creator_id, category_id, annotation) VALUES
('Emerging Programming Languages to Watch', NOW() - INTERVAL '2 days', 'MODERATION', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'john.doe@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Technology'),
 'A look at promising new programming languages gaining traction in the developer community'),

('Renewable Energy Breakthroughs', NOW() - INTERVAL '1 day', 'MODERATION', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'jane.smith@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Science'),
 'Recent innovations in solar, wind, and other renewable energy technologies');