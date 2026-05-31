create table instructors (
    id uuid primary key,
    tenant_id uuid not null,
    name varchar(255) not null,
    phone varchar(255)
);

create index idx_instructors_tenant_name on instructors(tenant_id, name);

create table places (
    id uuid primary key,
    tenant_id uuid not null,
    name varchar(255) not null,
    memo varchar(500)
);

create index idx_places_tenant_name on places(tenant_id, name);

insert into instructors (id, tenant_id, name, phone)
values ('00000000-0000-0000-0000-000000000101', '00000000-0000-0000-0000-000000000001', '기본 강사', null);

insert into places (id, tenant_id, name, memo)
values ('00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000001', '1번 강의실', '기본 수업 공간');
