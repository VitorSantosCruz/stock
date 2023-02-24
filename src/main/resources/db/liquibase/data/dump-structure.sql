-- Criar banco de dados "stock"
-- create database if not exists stock character set utf8mb4 collate utf8mb4_unicode_ci;

-- Remover usuário caso ele já exista
-- drop user if exists 'stock'@'localhost';

-- Criar usuário "stock" se não existir
-- create user if not exists 'stock'@'localhost' identified by '4]2gC6pt^L9!$bJ@CNim';

-- Dar privilégios de acesso ao banco "stock" para o usuário "stock"
-- grant all privileges on stock.* to 'stock'@'localhost';

-- Usar banco de dados "stock"
use stock;

-- Criar tabela "user"
create table if not exists user(
    created_date datetime not null default current_timestamp,
    last_modified_date datetime,
    id bigint primary key auto_increment,
    name varchar(255) not null,
    login varchar(255) not null unique,
    password varchar(255) not null,
    login_attemp_cont int not null default 0,
    blocked_until time,
    is_root  boolean default false,
    is_deleted boolean default false
);

-- Criar tabela "product"
create table if not exists product(
    created_date datetime not null default current_timestamp,
    last_modified_date datetime,
    id bigint primary key auto_increment,
    product_code varchar(255) not null unique,
    name varchar(255) not null,
    model varchar(255) not null,
    brand varchar(255) not null,
    price decimal(9,2) not null,
    is_deleted boolean default false,
    created_by bigint not null,
    foreign key (created_by) references user(id)
);

-- Criar tabela "sale"
create table if not exists sale(
    created_date datetime not null default current_timestamp,
    last_modified_date datetime,
    id bigint primary key auto_increment,
    price decimal(9,2) not null,
    form_of_payment enum('PIX', 'MONEY', 'CREDIT', 'DEBIT') not null,
    discount decimal(9,2) not null,
    seller_id bigint not null,
    foreign key (seller_id) references user(id)
);

-- Criar tabela "product_info"
create table if not exists product_info(
    created_date datetime not null default current_timestamp,
    last_modified_date datetime,
    id bigint primary key auto_increment,
    size varchar(255) not null,
    color varchar(255) not null
);

-- Criar tabela "stock"
create table if not exists stock(
    created_date datetime not null default current_timestamp,
    last_modified_date datetime,
    id bigint primary key auto_increment,
    is_deleted boolean default false,
    product_id bigint not null,
    product_info_id bigint not null,
    sale_id bigint,
    created_by bigint not null,
    foreign key (product_id) references product(id),
    foreign key (product_info_id) references product_info(id),
    foreign key (sale_id) references sale(id),
    foreign key (created_by) references user(id)
);

-- Insere dados na tabela "user"
insert into user (id, name, login, password, is_root) values (1, "Administraor", "admin", "c7ad44cbad762a5da0a452f9e854fdc1e0e7a52a38015f23f3eab1d80b931dd472634dfac71cd34ebc35d16ab7fb8a90c81f975113d6c7538dc69dd8de9077ec", true);