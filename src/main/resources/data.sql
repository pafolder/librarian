INSERT INTO book (author, title, location, amount)
VALUES ('First Author', 'First Book', 'Shelf 1', 0),
       ('Second Author', 'Second Book', 'Shelf 2', 0),
       ('Third Author', 'Third Book', 'Shelf 3', 0),
       ('Fourth Author', 'Fourth Book', 'Shelf 4', 0),
       ('Fifth Author', 'Fifth Book', 'Shelf 5', 1),
       ('Sixth Author', 'Sixth Book', 'Shelf 6', 1),
       ('Seventh Author', 'Seventh Book', 'Shelf 7', 0),
       ('Eighth Author', 'Eighth Book', 'Shelf 8', 0);

INSERT INTO users (name, email, password, role)
VALUES ('Administrator', 'admin@mail.com', '{noop}admin', 'ADMIN'),
       ('User', 'user@mail.com', '{noop}password', 'USER'),
       ('John Smith', 'johnsmith@mail.net', '{noop}password', 'USER'),
       ('Alexandre Henderson', 'ahen@nomail.in', '{noop}password', 'USER'),
       ('Kirill Kirillov', 'kkirillov@qup.org', '{noop}password', 'USER'),
       ('Eugeni Flaccid', 'evgeniflaccid.e@yahoo.zoo', '{noop}password', 'USER');

INSERT INTO checkout (user_id, book_id, checkout_date_time, checkin_date_time)
VALUES (1, 1, TIMESTAMPADD(SQL_TSI_DAY, -16, TIMESTAMPADD(SQL_TSI_HOUR, 3, CURRENT_TIMESTAMP)), NULL),
       (1, 2, TIMESTAMPADD(SQL_TSI_DAY, -13, TIMESTAMPADD(SQL_TSI_HOUR, 4, CURRENT_TIMESTAMP)), NULL),
       (1, 7, TIMESTAMPADD(SQL_TSI_DAY, -2, TIMESTAMPADD(SQL_TSI_HOUR, -1, CURRENT_TIMESTAMP)), NULL),
       (2, 3, TIMESTAMPADD(SQL_TSI_DAY, -14, TIMESTAMPADD(SQL_TSI_HOUR, -3, CURRENT_TIMESTAMP)), NULL),
       (2, 4, TIMESTAMPADD(SQL_TSI_DAY, 1, TIMESTAMPADD(SQL_TSI_HOUR, -2, CURRENT_TIMESTAMP)), NULL),
       (1, 1, TIMESTAMPADD(SQL_TSI_DAY, -22, TIMESTAMPADD(SQL_TSI_HOUR, 3, CURRENT_TIMESTAMP)),
        TIMESTAMPADD(SQL_TSI_DAY, -14, TIMESTAMPADD(SQL_TSI_HOUR, 3, CURRENT_TIMESTAMP))),
       (2, 3, '2022-12-14 18:34:10', '2022-12-27 11:38:30');
