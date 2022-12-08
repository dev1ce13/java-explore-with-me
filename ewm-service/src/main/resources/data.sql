INSERT INTO categories(name) VALUES ('Category 1');

INSERT INTO users(name, email) VALUES ('UserName1', 'UserEmail1@mail.ru'),
                                      ('UserName2', 'UserEmail2@mail.ru');

INSERT INTO events(annotation,
                   category_id,
                   confirmed_requests,
                   created_on,
                   description,
                   event_date,
                   initiator_id,
                   location_lat,
                   location_lon,
                   paid,
                   participant_limit,
                   published_on,
                   request_moderation,
                   state,
                   title,
                   views)
VALUES ('annotation1',
        1,
        1,
        '2020-12-12 12:12:12',
        'Description1',
        '2021-12-12 12:12:12',
        1,
        50.50,
        60.60,
        true,
        0,
        '2021-01-01 12:12:12',
        false,
        'PUBLISHED',
        'Title1',
        0);

INSERT INTO requests(created, event_id, requester_id, state) VALUES ('2021-02-02 12:12:12', 1, 2, 'CONFIRMED');