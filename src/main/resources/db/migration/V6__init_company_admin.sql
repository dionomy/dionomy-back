create table demo_requests (
    id uuid primary key,
    academy_name varchar(255) not null,
    business_type varchar(255) not null,
    academy_size varchar(255) not null,
    contact varchar(255) not null,
    created_at timestamp not null
);

create index idx_demo_requests_created on demo_requests(created_at desc);

create table cs_tickets (
    id uuid primary key,
    title varchar(255) not null,
    body text not null,
    contact varchar(255) not null,
    status varchar(32) not null,
    created_at timestamp not null
);

create index idx_cs_tickets_created on cs_tickets(created_at desc);
create index idx_cs_tickets_contact_created on cs_tickets(contact, created_at desc);

create table tenant_setups (
    id uuid primary key,
    academy_name varchar(255) not null,
    owner_contact varchar(255) not null,
    main_color varchar(7) not null,
    tenant_status varchar(32) not null,
    build_status varchar(32) not null,
    created_at timestamp not null
);

create index idx_tenant_setups_created on tenant_setups(created_at desc);
