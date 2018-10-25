CREATE SEQUENCE actions_id_seq;
CREATE SEQUENCE games_id_seq;

CREATE TABLE games(
    id integer DEFAULT nextval('games_id_seq'::regclass) PRIMARY KEY,
    initial jsonb
);

CREATE TABLE actions(
    id integer DEFAULT nextval('actions_id_seq'::regclass) PRIMARY KEY,
    game_id integer REFERENCES games(id),
    action jsonb
);
