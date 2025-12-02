create table if not exists items (
id                      bigint          generated always as identity primary key,
status                  item_status     not null default 'CURRENT',
name                    text            not null,
summary                 text            not null,
created_at              timestamptz     not null default now(),
last_modified_at        timestamptz     not null default now(),
discontinued_at         timestamptz     default null
);