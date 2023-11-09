create table if not exists tt_task
(
    id           bigserial not null primary key,
    task_name    varchar(255),
    description  varchar(255),
    task_created timestamp,
    task_start   timestamp,
    task_end     timestamp,
    is_completed boolean default false,
    project_id   bigint,
    creator_id   bigint,
    executor_id  bigint
);
