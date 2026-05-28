create table attendance_records (
    id uuid primary key,
    tenant_id uuid not null,
    session_id uuid not null,
    student_id uuid not null,
    status varchar(32) not null,
    checked_by_teacher_id uuid not null,
    checked_at timestamp not null
);

create index idx_attendance_records_tenant_session_checked on attendance_records(tenant_id, session_id, checked_at);
create index idx_attendance_records_tenant_student_checked on attendance_records(tenant_id, student_id, checked_at desc);

create table absence_requests (
    id uuid primary key,
    tenant_id uuid not null,
    student_id uuid not null,
    session_id uuid not null,
    reason varchar(500) not null,
    desired_result varchar(64) not null,
    status varchar(32) not null,
    requested_at timestamp not null,
    resolved_at timestamp
);

create index idx_absence_requests_tenant_requested on absence_requests(tenant_id, requested_at desc);
create index idx_absence_requests_tenant_student_requested on absence_requests(tenant_id, student_id, requested_at desc);
