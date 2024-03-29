INSERT INTO type_category (name_type) VALUES ('Витрати'), ('Надходження');
INSERT INTO category (name,type_category_id)
VALUES ('Вода',100000,'ua-UA'),
       ('Спорт',100000,'ua-UA'),
       ('Інтернет',100000,'ua-UA'),
       ('Ігри',100000,'ua-UA'),
       ('Стипендія',100001,'ua-UA'),
       ('Трейд',100001,'ua-UA'),
       ('Зарплата',100001,'ua-UA'),
       ('Допомога',100001,'ua-UA'),
       ('Прибуток від бізнесу',100001,'ua-UA'),
       ('Аліменти',100001,'ua-UA'),
       ('Повернення платежу',100001,'ua-UA'),
       ('Аванс',100001,'ua-UA'),
       ('Повернення податків',100001,'ua-UA'),
       ('Пенсія',100001,'ua-UA'),
       ('Приз (виграш)',100001,'ua-UA'),
       ('Подарунки (знахідки)',100001,'ua-UA'),
       ('Дивіденди',100001,'ua-UA'),
       ('Підробіток',100001,'ua-UA'),
       ('Відсотки по депозиту',100001,'ua-UA'),
       ('Фріланс',100001,'ua-UA'),
       ('Фьючерси',100001,'ua-UA'),
       ('Криптовалюта',100001,'ua-UA'),
       ('Продаж речей',100001,'ua-UA'),
       ('Премія',100001,'ua-UA'),
       ('Соціальна допомога',100001,'ua-UA'),
       ('Відрядні',100001,'ua-UA'),
       ('Інше',100001,'ua-UA'),
       ('Особистий транспорт',100000,'ua-UA'),
       ('Благодійність',100000,'ua-UA'),
       ('Побутова техніка',100000,'ua-UA'),
       ('Діти',100000,'ua-UA'),
       ('Домашні тварини',100000,'ua-UA'),
       ('Здоров''я та краса',100000,'ua-UA'),
       ('Кредити та позика',100000,'ua-UA'),
       ('Квартира',100000,'ua-UA'),
       ('Зв''язок',100000,'ua-UA'),
       ('Страхування',100000,'ua-UA'),
       ('Освіта',100000,'ua-UA'),
       ('Одяг та аксесуари',100000,'ua-UA'),
       ('Відпочинок та розваги',100000,'ua-UA'),
       ('Харчування',100000,'ua-UA'),
       ('Різне',100000,'ua-UA'),
       ('Товари для дому',100000,'ua-UA'),
       ('Транспорт',100000,'ua-UA'),
       ('Інформаційні технології',100000,'ua-UA'),
       ('Дача, сад, город',100000,'ua-UA');
INSERT INTO category  (name, type_category_id, locale) VALUES
('Deposits',100000,'en-UK'),
('Water',100000,'en-UK'),
('Sport',100000,'en-UK'),
('Ethernet',100000,'en-UK'),
('Games',100000,'en-UK'),
('Stipend',100001,'en-UK'),
('Trade',100001,'en-UK'),
('Salary',100001,'en-UK'),
('Assistance',100001,'en-UK'),
('Profit from business',100001,'en-UK'),
('Alimony',100001,'en-UK'),
('Payment refund',100001,'en-UK'),
('Advance',100001,'en-UK'),
('Tax refunds',100001,'en-UK'),
('Pension',100001,'en-UK'),
('Prize (win)',100001,'en-UK'),
('Gifts (finds)',100001,'en-UK'),
('Dividends',100001,'en-UK'),
('Part time work',100001,'en-UK'),
('Interest on the deposit',100001,'en-UK'),
('Freelance',100001,'en-UK'),
('Futures',100001,'en-UK'),
('Cryptocurrency',100001,'en-UK'),
('Sale of things',100001,'en-UK'),
('Premium',100001,'en-UK'),
('Social assistance',100001,'en-UK'),
('Business trips',100001,'en-UK'),
('Other',100001,'en-UK'),
('Personal transport',100000,'en-UK'),
('Charity',100000,'en-UK'),
('Household appliances',100000,'en-UK'),
('Childrens',100000,'en-UK'),
('Pets',100000,'en-UK'),
('Health and beauty',100000,'en-UK'),
('Credits and loans',100000,'en-UK'),
('Flat',100000,'en-UK'),
('Communication',100000,'en-UK'),
('Insurance',100000,'en-UK'),
('Education',100000,'en-UK'),
('Clothing and accessories',100000,'en-UK'),
('Recreation and entertainment',100000,'en-UK'),
('Food',100000,'en-UK'),
('Different',100000,'en-UK'),
('Household goods',100000,'en-UK'),
('Transport',100000,'en-UK'),
('Information technology',100000,'en-UK'),
('Cottage, garden, city',100000,'en-UK');
Insert Into operation (amount, note, category_id, is_regular, create_at, raw_text, type_operation_id, user_id, source_id)
values (10007,null,100004,false,'2020-04-10 12:10:25','100',100052,1,100049);
INSERT INTO source_info (type_source) VALUES ('Готівка'),('Кредитна карта');
INSERT INTO type_operation (name) values ('INCOME'),('EXPENSE');

