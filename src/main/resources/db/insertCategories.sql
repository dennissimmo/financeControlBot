INSERT INTO type_category (name_type) VALUES ('Витрати'), ('Надходження');
INSERT INTO category (name,type_category_id)
VALUES ('Продукти',100000),
       ('Транспорт',100000),
       ('Вода',100000),
       ('Спорт',100000),
       ('Інтернет',100000),
       ('Ігри',100000),
       ('Стипендія',100001),
       ('Трейд',100001),
       ('Зарплата',100001);
INSERT INTO source_info (type_source) VALUES ('Готівка'),('Кредитна карта');
INSERT INTO type_operation (name) values ('INCOME'),('EXPENSE');

