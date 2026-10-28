insert into roles (id, name)
select gen_random_uuid(), 'ROLE_USER'
where not exists(select 1 from roles where name = 'ROLE_USER');

insert into roles (id, name)
select gen_random_uuid(), 'ROLE_ADMIN'
where not exists(select 1 from roles where name = 'ROLE_ADMIN');