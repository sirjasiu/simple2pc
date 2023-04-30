CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

create schema if not exists simple2pc;

create table simple2pc.job
(
    id             uuid primary key,
    operation_name varchar(64) not null,
    state          varchar(16) not null,
    data           jsonb

);