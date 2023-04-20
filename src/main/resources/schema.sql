DROP TABLE IF EXISTS FILM_LIKE CASCADE;
DROP TABLE IF EXISTS GENRE CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS FILM CASCADE;
DROP TABLE IF EXISTS FILM_GENRE CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS USER_FRIEND CASCADE;

create table GENRE
(
    ID   INTEGER auto_increment
        primary key,
    NAME CHARACTER VARYING not null
);

create table MPA
(
    ID   INTEGER auto_increment
        primary key,
    NAME CHARACTER VARYING not null
);

create table FILM
(
    ID           INTEGER auto_increment
        primary key,
    NAME         CHARACTER VARYING not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    RATE         INTEGER default 0 not null,
    MPA_ID       INTEGER,
    constraint FILM_MPA_ID_FK
        foreign key (MPA_ID) references MPA
);

create table FILM_GENRE
(
    FILM_ID  INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE_FILM_ID_FK
        foreign key (FILM_ID) references FILM
            on delete cascade,
    constraint FILM_GENRE_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRE
);

create unique index FILM_GENRE_FILM_ID_GENRE_ID_UINDEX
    on FILM_GENRE (FILM_ID, GENRE_ID);

create table USERS
(
    ID       INTEGER auto_increment
        primary key,
    EMAIL    CHARACTER VARYING not null,
    LOGIN    CHARACTER VARYING not null,
    NAME     CHARACTER VARYING,
    BIRTHDAY DATE              not null
);

create table FILM_LIKE
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint FILM_LIKE_FILM_ID_FK
        foreign key (FILM_ID) references FILM
            on delete cascade,
    constraint FILM_LIKE_USERS_ID_FK
        foreign key (USER_ID) references USERS
            on delete cascade
);

create unique index FILM_LIKE_FILM_ID_USER_ID_UINDEX
    on FILM_LIKE (FILM_ID, USER_ID);

create table USER_FRIEND
(
    USER_ID     INTEGER               not null,
    FRIEND_ID   INTEGER               not null,
    IS_ACCEPTED BOOLEAN default FALSE not null,
    constraint USER_FRIEND_USERS_FRIEND_ID_ID
        foreign key (FRIEND_ID) references USERS
            on delete cascade,
    constraint USER_FRIEND_USERS_USER_ID_ID
        foreign key (USER_ID) references USERS
            on delete cascade
);

create unique index USER_FRIEND_USER_ID_FRIEND_ID_UINDEX
    on USER_FRIEND (USER_ID, FRIEND_ID);
