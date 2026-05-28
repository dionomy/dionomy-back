create table class_notes (
    id uuid primary key,
    tenant_id uuid not null,
    session_id uuid not null,
    teacher_id uuid not null,
    progress text not null,
    feedback text not null,
    next_assignment text not null,
    created_at timestamp not null
);

create index idx_class_notes_tenant_created on class_notes(tenant_id, created_at desc);
create index idx_class_notes_tenant_session_created on class_notes(tenant_id, session_id, created_at desc);

create table notices (
    id uuid primary key,
    tenant_id uuid not null,
    title varchar(255) not null,
    body text not null,
    image_url varchar(1000),
    target varchar(32) not null,
    class_id uuid,
    created_at timestamp not null
);

create index idx_notices_tenant_created on notices(tenant_id, created_at desc);
