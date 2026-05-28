create table class_sessions (
    id uuid primary key,
    tenant_id uuid not null,
    title varchar(255) not null,
    type varchar(32) not null,
    teacher_id uuid not null,
    place_id uuid,
    starts_at timestamp not null,
    ends_at timestamp not null,
    capacity_current integer not null,
    capacity_maximum integer not null,
    assigned_student_ids text not null,
    recurrence_frequency varchar(32),
    recurrence_days_of_week text,
    recurrence_until date
);

create index idx_class_sessions_tenant_starts_at on class_sessions(tenant_id, starts_at);
