insert into auth_task (id, title, describe, user_id) values(1, 'Study PlayFramework 2.0','http://www.playframework.org/', 2);
insert into auth_task (id, title, describe, user_id) values(2, 'Study Grails 2.0','http://www.grails.org/', 2);
insert into auth_task (id, title, describe, user_id) values(3, 'Try SpringFuse','http://www.springfuse.com/', 2);
insert into auth_task (id, title, describe, user_id) values(4, 'Try Spring Roo','http://www.springsource.org/spring-roo', 2);
insert into auth_task (id, title, describe, user_id) values(5, 'Release SpringSide 4.0','As soon as posibble.', 2);

insert into auth_user (id, login_name, name, password, salt, register_date) values(1,'admin','Admin','691b14d79bf0fa2215f155235df5e670b64394cc','7efbd59d9741d34f','2012-06-04 01:00:00');
insert into auth_user (id, login_name, name, password, salt, register_date) values(2,'user','Calvin','2488aa0c31c624687bd9928e0a5d29e7d1ed520b','6d65d24122c30500','2012-06-04 02:00:00');

insert into auth_role (id, name, parent_id, type, active, describe, register_date) values(0, '超级组', NULL, 'group', 'yes', '超级组','2012-06-04 02:00:00');
insert into auth_role (id, name, parent_id, type, active, describe, register_date) values(1, '管理员组', 0, 'group', 'yes', '管理员组','2012-06-04 02:00:00');
insert into auth_role (id, name, parent_id, type, active, describe, register_date) values(2, '操作员组', 0, 'group', 'yes', '操作员组','2012-06-04 02:00:00');
insert into auth_role (id, name, parent_id, type, active, describe, register_date) values(5, '角色1', 1, 'role', 'yes', '','2012-06-04 02:00:00');
insert into auth_role (id, name, parent_id, type, active, describe, register_date) values(6, '角色2', 1, 'role', 'yes', '','2012-06-04 02:00:00');
insert into auth_role (id, name, parent_id, type, active, describe, register_date) values(7, '角色3', 2, 'role', 'yes', '','2012-06-04 02:00:00');
insert into auth_role (id, name, parent_id, type, active, describe, register_date) values(8, '角色4', 2, 'role', 'yes', '','2012-06-04 02:00:00');

insert into auth_resource (id, name, parent_id, type, active, describe, register_date) values(0, '站点', NULL, '', 'yes', '','2012-06-04 02:00:00');
insert into auth_resource (id, name, parent_id, type, active, describe, register_date) values(1, '用户管理', 0, 'url', 'yes', '','2012-06-04 02:00:00');
insert into auth_resource (id, name, parent_id, type, active, describe, register_date) values(2, '资源管理', 0, 'url', 'yes', '','2012-06-04 02:00:00');
insert into auth_resource (id, name, parent_id, type, active, describe, register_date) values(3, '权限管理', 0, 'url', 'yes', '','2012-06-04 02:00:00');
