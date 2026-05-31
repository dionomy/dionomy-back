create table retention_signals (
    id uuid primary key,
    tenant_id uuid not null,
    student_id uuid not null,
    student_name varchar(255) not null,
    type varchar(64) not null,
    label varchar(255) not null,
    reason varchar(1000) not null,
    refreshed_at timestamp not null
);

create index idx_retention_signals_tenant_refreshed on retention_signals(tenant_id, refreshed_at desc, student_name);
