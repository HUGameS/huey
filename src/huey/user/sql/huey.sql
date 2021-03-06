-- :name insert-new-user :! :n
insert into user (user_id, login, name, bio, email, company, location, html_url, updated_at)
values (:user_id, :login, :name, :bio, :email, :company, :location, :html_url, :updated_at);

-- :name select-user-by-id :? :1
select * from user
where user_id = :user_id;

-- :name insert-user-role :! :n
insert into user_role (user_id, role)
values (:user_id, :role)

-- :name select-role-by-user-id :? :1
select * from user_role
where user_id = :user_id;

-- :name update-user-details-by-id :! :n
update user
set login = :login,
    name = :name,
    bio = :bio,
    email = :email,
    company = :company,
    location = :location,
    html_url = :html_url,
    updated_at = :updated_at
where user_id = :user_id;
