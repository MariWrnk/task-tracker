delete
from tt_user
where id = 0;
insert into tt_user
values (0, 'Default User');

delete
from tt_project
where id = 0;
insert into tt_project
values (0, 'Default Project', 'Default Project Description');

delete
from tt_user_to_project
where id = 0;
insert into tt_user_to_project
values (0, 0, 0);

delete
from tt_task
where id IN (0, 1, 2);
insert into tt_task (id, task_name, project_id, creator_id, executor_id)
values (0, 'task1', 0, 0, 0);
insert into tt_task (id, task_name, creator_id, executor_id)
values (1, 'task2', 0, 0);