create table students (
    id uuid primary key,
    tenant_id uuid not null,
    name varchar(255) not null,
    phone varchar(255) not null,
    memo text,
    tags text not null,
    created_at timestamp not null
);

create index idx_students_tenant_name on students(tenant_id, name);

create table pass_products (
    id uuid primary key,
    tenant_id uuid not null,
    name varchar(255) not null,
    total_count integer not null,
    valid_days integer not null,
    price bigint not null,
    created_at timestamp not null
);

create index idx_pass_products_tenant_name on pass_products(tenant_id, name);

create table student_passes (
    id uuid primary key,
    tenant_id uuid not null,
    product_id uuid not null,
    student_id uuid not null,
    total_count integer not null,
    used_count integer not null,
    issued_on date not null,
    expires_on date not null
);

create index idx_student_passes_tenant_student_expires on student_passes(tenant_id, student_id, expires_on desc);

create table pass_usage_logs (
    id uuid primary key,
    tenant_id uuid not null,
    pass_id uuid not null,
    student_id uuid not null,
    type varchar(32) not null,
    count integer not null,
    reason varchar(500) not null,
    created_at timestamp not null
);

create index idx_pass_usage_logs_tenant_pass_created on pass_usage_logs(tenant_id, pass_id, created_at desc);
