-- Добавление 10 пользователей с ролью USER
INSERT INTO users (first_name, last_name, email, password, role) VALUES
('Иван', 'Петров', 'ivan.petrov@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Анна', 'Смирнова', 'anna.smirnova@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Михаил', 'Иванов', 'mikhail.ivanov@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Елена', 'Кузнецова', 'elena.kuznetsova@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Дмитрий', 'Соколов', 'dmitry.sokolov@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Ольга', 'Попова', 'olga.popova@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Алексей', 'Васильев', 'alexey.vasiliev@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Мария', 'Новикова', 'maria.novikova@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Сергей', 'Морозов', 'sergey.morozov@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER'),
('Екатерина', 'Волкова', 'ekaterina.volkova@example.com', '$2a$10$3QbDb4rLp9DCfNZ3XtynKOCXB8GDmXJEZ.K/mUCokF4oHKzEVuRKO', 'USER');

-- Добавление scope user.read и article.read для всех пользователей
INSERT INTO user_scopes (user_id, scopes)
SELECT user_id, 'user.read' FROM users WHERE role = 'USER';

INSERT INTO user_scopes (user_id, scopes)
SELECT user_id, 'article.read' FROM users WHERE role = 'USER';

-- Добавление scope article.write для пользователей, создающих статьи
INSERT INTO user_scopes (user_id, scopes) VALUES
((SELECT user_id FROM users WHERE email = 'ivan.petrov@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'anna.smirnova@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'mikhail.ivanov@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'elena.kuznetsova@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'dmitry.sokolov@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'olga.popova@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'alexey.vasiliev@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'maria.novikova@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'sergey.morozov@example.com'), 'article.write'),
((SELECT user_id FROM users WHERE email = 'ekaterina.volkova@example.com'), 'article.write');

-- Добавление категорий, связанных с экологией
INSERT INTO categories (name, description) VALUES
('Климат', 'Статьи о климатических изменениях и их последствиях'),
('Биоразнообразие', 'Исследования и защита биологического разнообразия'),
('Энергия', 'Возобновляемые источники энергии и энергосбережение'),
('Устойчивое развитие', 'Методы и практики устойчивого развития'),
('Экологические технологии', 'Технологические решения для защиты окружающей среды');

-- Добавление 20 статей на экологическую тематику
INSERT INTO articles (title, creation_date, article_status, paid_status, creator_id, category_id, annotation) VALUES
-- Климат
('Будущее климатической политики в России', NOW() - INTERVAL '30 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'ivan.petrov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Климат'),
 'Обзор новых стратегий России в области климатической политики и их влияния на окружающую среду'),

('Последствия таяния ледников в Арктике', NOW() - INTERVAL '28 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'anna.smirnova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Климат'),
 'Как таяние арктических ледников влияет на экосистемы и глобальный климат'),

('Климатические беженцы: вызовы XXI века', NOW() - INTERVAL '25 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'mikhail.ivanov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Климат'),
 'Проблемы, связанные с перемещением населения из-за климатических изменений'),

('Углеродный след: как его сократить', NOW() - INTERVAL '21 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'elena.kuznetsova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Климат'),
 'Практические советы по снижению углеродного следа для частных лиц и компаний'),

-- Биоразнообразие
('Сохранение редких видов в Сибири', NOW() - INTERVAL '19 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'dmitry.sokolov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Биоразнообразие'),
 'Инициативы по защите редких видов животных и растений в сибирских лесах'),

('Роль пчел в экосистемах', NOW() - INTERVAL '18 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'ivan.petrov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Биоразнообразие'),
 'Почему пчелы важны для сохранения биоразнообразия и сельского хозяйства'),

('Морские заповедники: защита океанов', NOW() - INTERVAL '17 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'olga.popova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Биоразнообразие'),
 'Роль морских заповедников в сохранении биоразнообразия океанов'),

('Инвазивные виды: угроза экосистемам', NOW() - INTERVAL '15 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'mikhail.ivanov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Биоразнообразие'),
 'Как инвазивные виды влияют на местные экосистемы и как с ними бороться'),

-- Энергия
('Солнечная энергия в России: перспективы', NOW() - INTERVAL '14 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'alexey.vasiliev@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Энергия'),
 'Развитие солнечной энергетики в России и ее потенциал'),

('Ветроэнергетика: новые технологии', NOW() - INTERVAL '13 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'anna.smirnova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Энергия'),
 'Инновации в области ветроэнергетики и их экологические преимущества'),

('Энергоэффективность зданий', NOW() - INTERVAL '12 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'maria.novikova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Энергия'),
 'Как энергоэффективные здания снижают потребление энергии'),

('Гидроэнергия: плюсы и минусы', NOW() - INTERVAL '11 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'sergey.morozov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Энергия'),
 'Анализ преимуществ и недостатков гидроэнергетики'),

-- Устойчивое развитие
('Экологичный образ жизни: с чего начать', NOW() - INTERVAL '10 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'ekaterina.volkova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Устойчивое развитие'),
 'Практические шаги для перехода к устойчивому образу жизни'),

('Экономика замкнутого цикла', NOW() - INTERVAL '9 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'ivan.petrov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Устойчивое развитие'),
 'Как экономика замкнутого цикла способствует устойчивому развитию'),

('Зеленые города: будущее урбанизации', NOW() - INTERVAL '8 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'maria.novikova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Устойчивое развитие'),
 'Концепции зеленых городов и их роль в устойчивом развитии'),

('Корпоративная социальная ответственность', NOW() - INTERVAL '7 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'anna.smirnova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Устойчивое развитие'),
 'Как компании внедряют экологические инициативы в свою деятельность'),

-- Экологические технологии
('Технологии переработки пластика', NOW() - INTERVAL '6 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'dmitry.sokolov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Экологические технологии'),
 'Новые методы переработки пластиковых отходов'),

('Умные системы управления отходами', NOW() - INTERVAL '5 days', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'olga.popova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Экологические технологии'),
 'Как умные технологии помогают эффективно управлять отходами'),

('Экологичные строительные материалы', NOW() - INTERVAL '3 days', 'GRANTED', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'sergey.morozov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Экологические технологии'),
 'Использование экологичных материалов в строительстве'),

('ИИ для мониторинга окружающей среды', NOW() - INTERVAL '1 day', 'GRANTED', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'ivan.petrov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Экологические технологии'),
 'Применение искусственного интеллекта для контроля экологических показателей');

-- Добавление статей в статусе MODERATION
INSERT INTO articles (title, creation_date, article_status, paid_status, creator_id, category_id, annotation) VALUES
('Инновации в очистке водоемов', NOW() - INTERVAL '2 days', 'MODERATION', 'FREE', 
 (SELECT user_id FROM users WHERE email = 'elena.kuznetsova@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Экологические технологии'),
 'Новые подходы к очистке водоемов от загрязнений'),

('Возобновляемая энергия в сельских регионах', NOW() - INTERVAL '1 day', 'MODERATION', 'PAID', 
 (SELECT user_id FROM users WHERE email = 'mikhail.ivanov@example.com'),
 (SELECT category_id FROM categories WHERE name = 'Энергия'),
 'Как возобновляемая энергия может улучшить жизнь в сельских регионах');