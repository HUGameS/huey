-- 001-create-user-and-role.up.sql
create table user (
  user_id int(11) not null,
  login varchar(64),
  name varchar(64),
  bio varchar(256),
  email varchar(64),
  company varchar(64),
  location varchar(64),
  html_url varchar(64),
  updated_at datetime(3),
  primary key (user_id)
)

--;;

create table user_role (
  user_role_id int(11) not null auto_increment,
  user_id int(11) not null,
  role varchar(64) not null,
  primary key (user_role_id),
  foreign key (user_id) references user(user_id)
)
