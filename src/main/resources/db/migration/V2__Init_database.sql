create table `groups`
(
    id   bigint       not null auto_increment,
    name varchar(255) not null,
    primary key (id)
);

create table group_permissions
(
    permission_id bigint not null,
    group_id      bigint not null
);

create table permissions
(
    id   bigint       not null auto_increment,
    name varchar(255) not null,
    primary key (id)
);

create table users
(
    id       bigint       not null auto_increment,
    login    varchar(255) not null,
    password varchar(255) not null,
    group_id bigint,
    primary key (id)
);

create table user_permissions
(
    permission_id bigint not null,
    user_id       bigint not null,
    enabled       TINYINT default 0,
    primary key (permission_id, user_id)
);