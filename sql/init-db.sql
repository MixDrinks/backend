create table tags
(
    id   integer not null
        constraint tags_pk
            primary key,
    name text    not null,
    type integer,
    slug text    not null
        constraint tags_pk_slug
            unique
);

create table cocktails_to_tags
(
    tag_id      integer not null,
    cocktail_id integer
);

create table cocktails
(
    id           integer           not null
        constraint cocktails_pk
            primary key,
    name         text,
    recipe       text[],
    visit_count  integer default 0 not null,
    rating_count integer default 0 not null,
    rating_value integer,
    slug         text              not null
        constraint cocktails_pk_slug
            unique
);

create table goods
(
    name        text,
    about       text,
    id          integer           not null
        constraint goods_pk
            primary key,
    visit_count integer default 0 not null,
    slug        text              not null
        constraint goods_pk_slug
            unique
);

create table cocktails_to_items
(
    cocktail_id integer not null,
    good_id     integer not null,
    amount      integer not null,
    unit        text,
    relation    integer
);

create index cocktail_index
    on cocktails_to_items (cocktail_id, relation);

create table tastes
(
    id   integer not null
        constraint tastes_pk
            primary key,
    name text    not null,
    slug text    not null
        constraint tastes_pk_slug
            unique
);

create table cocktails_to_tastes
(
    cocktail_id integer not null
        constraint cocktails_to_tastes_cocktails_null_fk
            references cocktails,
    taste_id    integer not null
        constraint cocktails_to_tastes_tastes_null_fk
            references tastes
);

create table alcohol_volumes
(
    id   integer not null
        constraint alcohol_volumes_pk
            primary key,
    name text,
    slug text    not null
        constraint alcohol_volumes_pk_slug
            unique
);

create table cocktails_to_alcohol_volume
(
    alcohol_volume_id integer
        constraint table_name_alcohol_volumes_null_fk
            references alcohol_volumes,
    cocktail_id       integer not null
        constraint table_name_cocktails_null_fk
            references cocktails
);

create table tools
(
    id          integer           not null
        constraint tools_pk
            primary key,
    name        text              not null,
    about       text              not null,
    visit_count integer default 0 not null,
    slug        text              not null
        constraint tools_pk2
            unique
);

create table cocktails_to_tools
(
    cocktail_id integer not null
        constraint cocktalis_to_tools_cocktails_null_fk
            references cocktails,
    tool_id     integer not null
        constraint cocktalis_to_tools_tools_null_fk
            references tools
);

create table glassware
(
    id    integer not null
        constraint glassware_pk
            primary key,
    name  text    not null,
    about text    not null,
    slug  text    not null
        constraint glassware_pk_slug
            unique
);

create table cocktails_to_glassware
(
    cocktail_id  integer not null
        constraint cocktails_to_glassware_cocktails_null_fk
            references cocktails,
    glassware_id integer not null
        constraint cocktails_to_glassware_glassware_null_fk
            references glassware
);

create table redirects
(
    "from" text,
    "to"   text
);

create table users
(
    user_id text not null
        constraint users_pk
            primary key
);

insert into tags (id, name, type, slug)
values (1, 'Tag1', 1, 'tag_1');

insert into cocktails (id, name, recipe, visit_count, rating_count, rating_value, slug)
values (1, 'Cocktail1', '{"1", "2", "3"}', 1, 1, 1, 'cocktail_1');

insert into cocktails (id, name, recipe, visit_count, rating_count, rating_value, slug)
values (2, 'Cocktail2', '{"1_1", "2_2", "3_3"}', 1, 1, 1, 'cocktail_2');

insert into goods (id, name, about, visit_count, slug)
values (1, 'Good1', 'About1', 1, 'good_1');

insert into tastes (id, name, slug)
values (1, 'Taste1', 'taste_1');

insert into alcohol_volumes (id, name, slug)
values (1, 'AlcoholVolume1', 'slug_1');

insert into tools (id, name, about, visit_count, slug)
values (1, 'Tool1', 'About1', 1, 'tool_1'),
       (2, 'Tool2', 'About2', 1, 'tool_2');

insert into cocktails_to_alcohol_volume (alcohol_volume_id, cocktail_id)
values (1, 1);

insert into cocktails_to_items (cocktail_id, good_id, amount, unit)
values (1, 1, 1, 'ml');

insert into cocktails_to_tags (tag_id, cocktail_id)
values (1, 1);

insert into cocktails_to_tools (cocktail_id, tool_id)
values (1, 1);

insert into cocktails_to_tastes (cocktail_id, taste_id)
values (1, 1);

insert into glassware (id, name, about, slug)
values (100, 'Glassware1', 'About1', 'slug_1');

insert into cocktails_to_glassware (cocktail_id, glassware_id)
values (1, 100);

insert into cocktails_to_glassware (cocktail_id, glassware_id)
values (2, 100);
