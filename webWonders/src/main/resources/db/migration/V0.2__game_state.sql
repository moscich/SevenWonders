CREATE SEQUENCE gameStates_id_seq;

CREATE TABLE gameStates(
    id integer DEFAULT nextval('gameStates_id_seq'::regclass) PRIMARY KEY,
    game_id integer REFERENCES games(id),
    wonders jsonb
);
