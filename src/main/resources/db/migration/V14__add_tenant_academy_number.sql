alter table tenants add column academy_number integer;

update tenants
set academy_number = 1
where id = '00000000-0000-0000-0000-000000000001';

with numbered as (
    select id, row_number() over (order by name, id) + 1 as academy_number
    from tenants
    where academy_number is null
)
update tenants
set academy_number = numbered.academy_number
from numbered
where tenants.id = numbered.id;

alter table tenants alter column academy_number set not null;

create unique index idx_tenants_academy_number on tenants(academy_number);
