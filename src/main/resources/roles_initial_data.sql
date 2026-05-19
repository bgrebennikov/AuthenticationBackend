
insert into roles (name)
select 'ROLE_USER'
where not exists(select 1 from roles where name ='ROLE_USER');

insert into roles (name)
select 'ROLE_ADMIN'
where not exists(select 1 from roles where name = 'ROLE_ADMIN');