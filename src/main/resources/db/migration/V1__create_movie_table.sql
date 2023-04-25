CREATE TABLE movies (
    id   UUID PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE,
    year INTEGER NULL
);

CREATE INDEX movies_year_index ON movies(year);
CREATE INDEX movies_name_index ON movies(name);

CREATE TABLE actors (
    id   UUID PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE TABLE casting (
    movie_id UUID NOT NULL,
    actor_id UUID NOT NULL,
    CONSTRAINT fk_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_actor FOREIGN KEY (actor_id) REFERENCES actors(id),
    PRIMARY KEY(movie_id, actor_id)
);

CREATE TABLE genre (
    id   SMALLINT PRIMARY KEY,
    name VARCHAR NOT NULL UNIQUE
);

CREATE INDEX genre_name_index ON genre(name);

CREATE TABLE genere_movies (
    movie_id UUID NOT NULL,
    genre_id SMALLINT NOT NULL,
    CONSTRAINT fk_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_genre FOREIGN KEY (genre_id) REFERENCES genre(id),
    PRIMARY KEY (movie_id, genre_id)
)