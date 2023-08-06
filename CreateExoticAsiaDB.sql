drop database if exists ExoticAsia;

create database ExoticAsia;

use ExoticAsia;

CREATE TABLE main_menu (
  id VARCHAR(255) PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  status VARCHAR(255) NOT NULL
);

CREATE TABLE levelinfo (
    access_level INT PRIMARY KEY,
    level_name enum('Manager', 'Customer', 'VIP Customer') NOT NULL
);

INSERT INTO levelinfo
values (1, 'Manager'), (2, 'Customer'), (3, 'VIP Customer');

CREATE TABLE menu_access (
    access_level int not null ,
    id varchar(255) not null ,
    foreign key (access_level) references levelinfo(access_level) ,
    foreign key (id) references main_menu(id)
);

CREATE TABLE account (
    account VARCHAR(255) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    access_level INT default 2 not null ,
    last_login_time TIMESTAMP,
    foreign key (access_level) references levelinfo(access_level)
);

CREATE TABLE page (
    page_html VARCHAR(255) PRIMARY KEY,
    page_name VARCHAR(255) NOT NULL ,
    page_id VARCHAR(255) NOT NULL ,
    prev_id VARCHAR(255) NOT NULL ,
    prev_name VARCHAR(255) NOT NULL ,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE access_level (
    access_level int not null ,
    page_html VARCHAR(255) not null ,
    primary key (access_level, page_html) ,
    foreign key (access_level) references levelinfo(access_level) ,
    foreign key (page_html) references page(page_html)
);

create table user (
    id int primary key ,
    firstName varchar(255) not null ,
    lastName varchar(255) not null ,
    account varchar(255) not null ,
    foreign key (account) references account(account)
    );

create table store (
    id int primary key ,
    uid int not null ,
    name varchar(255) not null ,
    street varchar(255) not null ,
    state varchar(255) not null ,
    zipcode int not null ,
    foreign key (uid) references user(id)
);

create table productCategory (
    id int primary key ,
    category enum('Fruit', 'Vegetable', 'Condiment', 'Spice', 'Frozen', 'Snacks', 'Other')
);

INSERT INTO productCategory
values (1, 'Fruit'), (2, 'Vegetable'), (3, 'Condiment'), (4, 'Spice'), (6, 'Frozen'), (7, 'Snacks'), (8, 'Other');

create table products (
    id int primary key ,
    name varchar(255) not null ,
    pcid int not null ,
    foreign key (pcid) references productCategory(id)
);

create table StoreHasProducts (
    sid int ,
    pid int ,
    stock int not null default 0 ,
    price decimal(6, 2) not null default 0.00,
    vipDiscount int(3) not null default  0,
    primary key (sid, pid) ,
    foreign key (sid) references store(id) ,
    foreign key (pid) references products(id)
);

create table orderHistory (
    id int primary key ,
    uid int not null ,
    date timestamp default current_timestamp ,
    foreign key (uid) references user(id)
);

create table receipt (
    ohid int ,
    pid int ,
    quantity int not null ,
    primary key (ohid, pid) ,
    foreign key (ohid) references orderHistory(id) ,
    foreign key (pid) references products(id)
)