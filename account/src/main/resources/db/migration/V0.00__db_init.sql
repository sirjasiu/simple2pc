CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

create schema if not exists account;

create table account.account
(
    id    uuid primary key,
    funds numeric(12, 2),
    name  varchar(128)

);