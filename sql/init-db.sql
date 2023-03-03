create table tags
(
    id   integer,
    name text not null,
    type integer
);

insert into tags (id, name, type)
values (1, 'Tag1', 1);

create table cocktails
(
    id           integer
        constraint cocktails_pk
            primary key,
    name         text,
    recipe       text[],
    visit_count  integer default 0 not null,
    rating_count integer default 0 not null,
    rating_value integer
);

insert into cocktails (id, name, recipe, visit_count, rating_count, rating_value)
values (1, 'Cocktail1', '{"1", "2", "3"}', 1, 1, 1);


create table cocktails_to_tags
(
    tag_id      integer not null,
    cocktail_id integer
);

insert into cocktails_to_tags (tag_id, cocktail_id)
values (1, 1);

create table goods
(
    name        text,
    about       text,
    id          integer,
    relation    integer default 1,
    visit_count integer default 0 not null
);

insert into goods (id, name, about, relation, visit_count)
values (1, 'Good1', 'About1', 1, 1);

create table cocktails_to_items
(
    cocktail_id integer not null,
    good_id     integer not null,
    amount      integer not null,
    unit        text,
    relation    integer
);

insert into cocktails_to_items (cocktail_id, good_id, amount, unit, relation)
values (1, 1, 1, 'ml', 1);

create table tastes
(
    id   integer not null
        constraint tastes_pk
            primary key,
    name text    not null
);

insert into tastes (id, name)
values (1, 'Taste1');

create table cocktails_to_tastes
(
    cocktail_id integer not null
        constraint cocktails_to_tastes_cocktails_null_fk
            references cocktails,
    taste_id    integer not null
        constraint cocktails_to_tastes_tastes_null_fk
            references tastes
);

insert into cocktails_to_tastes (cocktail_id, taste_id)
values (1, 1);

create table alcohol_volumes
(
    id   integer not null
        constraint alcohol_volumes_pk
            primary key,
    name text
);

insert into alcohol_volumes (id, name)
values (1, 'AlcoholVolume1');

create table cocktails_to_alcohol_volume
(
    alcohol_volume_id integer
        constraint table_name_alcohol_volumes_null_fk
            references alcohol_volumes,
    cocktail_id       integer not null
        constraint table_name_cocktails_null_fk
            references cocktails
);

insert into cocktails_to_alcohol_volume (alcohol_volume_id, cocktail_id)
values (1, 1);

