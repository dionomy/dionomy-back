create table tenants (
    id uuid primary key,
    name varchar(255) not null,
    status varchar(32) not null
);

create table academy_settings (
    tenant_id uuid primary key,
    name varchar(255) not null,
    contact varchar(255) not null,
    address varchar(500) not null,
    logo_url varchar(1000),
    main_color varchar(7) not null,
    extension_allowed boolean not null,
    refund_allowed boolean not null,
    makeup_enabled boolean not null,
    makeup_expires_in_days integer not null,
    makeup_max_count integer not null
);
