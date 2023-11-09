create table if not exists tt_project
(
    id          bigserial not null primary key,
    name        varchar(255),
    description varchar(255)
);

create table if not exists tt_user_to_project
(
    id         bigserial not null primary key,
    user_id    bigint,
    project_id bigint
);