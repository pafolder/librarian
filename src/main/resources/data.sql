INSERT INTO book (author, title, location, amount)
VALUES ('First Author', 'First Book', 'Shelf 1', 1),
       ('Second Author', 'Second Book', 'Shelf 2', 1),
       ('Third Author', 'Third Book', 'Shelf 3', 1),
       ('Fourth Author', 'Forth Book', 'Shelf 4', 1),
       ('Fifth Author', 'Fifth Book', 'Shelf 5', 3);

INSERT INTO users (name, email, password, role)
VALUES ('Administrator', 'admin@mail.com', '{noop}admin', 'ADMIN'),
       ('User', 'user@mail.com', '{noop}password', 'USER'),
       ('John Smith', 'johnsmith@mail.net', '{noop}password', 'USER'),
       ('Alexandre Henderson', 'ahen@nomail.in', '{noop}password', 'USER'),
       ('Kirill Kirillov', 'kkirillov@qup.org', '{noop}password', 'USER'),
       ('Eugeni Flaccid', 'evgeniflaccid.e@yahoo.zoo', '{noop}password', 'USER');

INSERT INTO checkout (user_id, book_id, checkout_date_time, checkin_date_time)
VALUES (1, 1, '2022-12-12 21:34:10', NULL),
       (2, 2, '2022-12-12 21:34:10', NULL),
       (2, 3, '2022-12-14 21:34:10', NULL);
