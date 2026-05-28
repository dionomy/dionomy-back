create table care_records (
    id uuid primary key,
    tenant_id uuid not null,
    student_id uuid not null,
    memo varchar(1000) not null,
    status varchar(32) not null,
    created_at timestamp not null
);

create index idx_care_records_tenant_student on care_records (tenant_id, student_id, created_at desc);
