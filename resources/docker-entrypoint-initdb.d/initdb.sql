create database if not exists huey_dev character set utf8mb4 collate utf8mb4_unicode_ci;
grant all privileges on huey_dev.* to 'huey_dev_user' identified by 'password';

create database if not exists huey_test character set utf8mb4 collate utf8mb4_unicode_ci;
grant all privileges on huey_test.* to 'huey_test_user' identified by 'password';
