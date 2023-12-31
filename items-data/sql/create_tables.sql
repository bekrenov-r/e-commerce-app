-- \c items_data

drop table if exists item cascade;
drop table if exists item_details cascade;
drop table if exists unique_item cascade;
drop table if exists category cascade;
drop table if exists subcategory cascade;
drop table if exists image cascade;
drop table if exists review cascade;
drop table if exists size cascade;
drop table if exists color cascade;
drop table if exists item_size cascade;
drop table if exists item_color cascade;

create table category (
  name text,
  id serial primary key
);

create table subcategory (
  name text,
  category_id int,
  id serial primary key,
  foreign key (category_id) references category(id)
);

create table size (
    value text,
    type text,
    id serial primary key
);

create table color (
    value text,
    id serial primary key
);

CREATE TABLE item (
  name text,
  description text,
  price numeric,
  discount numeric,
  price_after_discount numeric,
  category_id int,
  subcategory_id int,
  gender text,
  age_group text,
  collection text,
  brand text,
  material text,
  season text,
  rating numeric,
  reviews_count int,
  item_code text,
  id serial PRIMARY KEY,
  foreign key (category_id) references category(id),
  foreign key (subcategory_id) references subcategory(id)
);

create table item_size (
  item_id bigint,
  size_id int,
  foreign key (item_id) references item(id),
  foreign key (size_id) references size(id)
);

create table item_color (
    item_id bigint,
    color_id int,
    foreign key (item_id) references item(id),
    foreign key (color_id) references color(id)
);

create table image (
    item_id bigint,
    color_id int,
    path text,
    id serial primary key,
    foreign key (item_id) references item(id)
);

CREATE TABLE item_details (
  item_id int,
  orders_count_total bigint,
  orders_count_last_month int,
  quantity int,
  created_at timestamp,
  updated_at timestamp,
  creating_employee_id int,
  updating_employee_id int,
  FOREIGN KEY (item_id) REFERENCES item (id)
);

CREATE TABLE unique_item (
  item_id int,
  size_id int,
  color_id int,
  weight_kg numeric,
  bar_code text,
  quantity int,
  restock_quantity int,
  reorder_quantity int,
  id serial PRIMARY KEY,
  FOREIGN KEY (item_id) REFERENCES item (id)
);

create table review (
    item_id int,
    customer_id int,
    created_at timestamp,
    content text,
    rating smallint,
    likes_count int,
    dislikes_count int,
    id serial primary key
);

insert into category values
                         ('T_SHIRTS'),
                         ('SHIRTS'),
                         ('TROUSERS'),
                         ('SHORTS'),
                         ('HOODIES_AND_SWEATSHIRTS'),
                         ('SWEATERS'),
                         ('COATS'),
                         ('JACKETS'),
                         ('SHOES'),
                         ('UNDERWEAR'),
                         ('SOCKS');

insert into subcategory values
                            ('JEANS', 3),
                            ('JOGGERS', 3),
                            ('SPORT', 3),
                            ('SANDALS', 9),
                            ('SNEAKERS', 9),
                            ('BOOTS', 9);

insert into size values('XS', 'clothes'), ('S', 'clothes'), ('M', 'clothes'), ('L', 'clothes'), ('XL', 'clothes'), ('2XL', 'clothes'), ('3XL', 'clothes'), ('4XL', 'clothes'),
                       ('36', 'shoes'), ('37', 'shoes'), ('38', 'shoes'), ('39', 'shoes'), ('40', 'shoes'), ('41', 'shoes'), ('42', 'shoes'), ('43', 'shoes'), ('44', 'shoes'), ('45', 'shoes');

insert into color values('black'), ('white'), ('red'), ('yellow'), ('green'), ('blue'), ('violet'), ('grey'), ('multi');