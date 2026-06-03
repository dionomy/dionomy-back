alter table academy_settings
    add column owner_schedule_enabled boolean not null default true,
    add column owner_students_enabled boolean not null default true,
    add column owner_notices_enabled boolean not null default true,
    add column teacher_mode_enabled boolean not null default true,
    add column student_pass_enabled boolean not null default true,
    add column student_class_notes_enabled boolean not null default true,
    add column student_absence_request_enabled boolean not null default true,
    add column crm_enabled boolean not null default true;
