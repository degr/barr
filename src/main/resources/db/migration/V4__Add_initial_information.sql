insert into permissions (name)
values ('admin');

insert into permissions (name)
values ('moderator');

insert into permissions (name)
values ('owner');

insert into `groups` (name)
values ('admin');

insert into `groups`(name)
values ('owner');

insert into users(login, password, group_id)
VALUES ('Oleg', '$2a$10$19.TUjECs76KbXEhJE4awutW9EOnv8lbq9Ya3q7pkYKn7ppukSueq', 2);

insert into users(login, password, group_id)
values ('Test', '$2a$10$DqD9WIKBk8ffMJO8Mz266OTRvHgA9yFYZOchdhPq2kU5nqUmQ9rE2', 1);
