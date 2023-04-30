CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

create schema if not exists offer;

create table offer.offer
(
    id          uuid primary key,
    price       numeric(12, 2) not null,
    name        varchar(128)   not null,
    reservation bool default false,
    account_id  uuid
);