create table instructor_availabilities (
    id uuid primary key,
    tenant_id uuid not null,
    instructor_id uuid not null,
    starts_at timestamp not null,
    ends_at timestamp not null,
    memo varchar(500)
);

create index idx_instructor_availabilities_tenant_starts
    on instructor_availabilities(tenant_id, starts_at);

create index idx_instructor_availabilities_instructor_starts
    on instructor_availabilities(tenant_id, instructor_id, starts_at);

insert into instructor_availabilities (id, tenant_id, instructor_id, starts_at, ends_at, memo)
values (
    '00000000-0000-0000-0000-000000000301',
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000101',
    current_date + time '18:00',
    current_date + time '22:00',
    '평일 저녁 보강 가능'
);
