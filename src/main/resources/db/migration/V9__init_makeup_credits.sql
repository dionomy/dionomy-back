alter table absence_requests
    add column resolved_target_session_id uuid;

create table makeup_credits (
    id uuid primary key,
    tenant_id uuid not null,
    absence_request_id uuid not null,
    student_id uuid not null,
    source_session_id uuid not null,
    status varchar(32) not null,
    expires_on date not null,
    created_at timestamp not null
);

create index idx_makeup_credits_tenant_student_created on makeup_credits(tenant_id, student_id, created_at desc);
