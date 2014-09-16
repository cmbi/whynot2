CREATE SEQUENCE hibernate_sequence MINVALUE 10000 START 10000;

CREATE TABLE databank
(
  id integer DEFAULT nextval('hibernate_sequence') PRIMARY KEY,
  crawltype character varying(255) NOT NULL,
  filelink character varying(200) NOT NULL,
  name character varying(50) NOT NULL,
  reference character varying(200) NOT NULL,
  regex character varying(50) NOT NULL,
  parent_id integer,
  CONSTRAINT fk_8kpckeduy4ic6f1wxn67g8erw FOREIGN KEY (parent_id)
      REFERENCES databank (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_mihh4vxgjrfcvyobhuykk6655 UNIQUE (name)
);


CREATE TABLE comment
(
  id integer DEFAULT nextval('hibernate_sequence') PRIMARY KEY,
  text character varying(200) NOT NULL,
  CONSTRAINT uk_1muo5gydw8e6lp31pdvxjocw UNIQUE (text)
);

CREATE TABLE file
(
  id integer DEFAULT nextval('hibernate_sequence') PRIMARY KEY,
  path character varying(200) NOT NULL,
  "timestamp" integer NOT NULL,
  CONSTRAINT uk_7yvbdpr06gn8glmeo1129bhkn UNIQUE (path, "timestamp")
);

CREATE TABLE entry
(
  id integer DEFAULT nextval('hibernate_sequence') PRIMARY KEY,
  pdbid character varying(10) NOT NULL,
  databank_id integer NOT NULL,
  file_id integer,
  CONSTRAINT fk_ao98w5gq978338lyjiohcjslg FOREIGN KEY (file_id)
      REFERENCES file (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_s2rccn40ja0id1h63116621y3 FOREIGN KEY (databank_id)
      REFERENCES databank (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_ejlrucnkm1jn2kmknjk1s9v02 UNIQUE (databank_id, pdbid)
);

CREATE TABLE annotation
(
  id integer DEFAULT nextval('hibernate_sequence') PRIMARY KEY,
  "timestamp" integer,
  comment_id integer NOT NULL,
  entry_id integer NOT NULL,
  CONSTRAINT fk_fr6et1a7sae38d66krtodj4om FOREIGN KEY (comment_id)
      REFERENCES comment (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_q8cbj0wr21o90s8udb0bya2pe FOREIGN KEY (entry_id)
      REFERENCES entry (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT uk_cw61780jkwdehpyhkv7atnnhy UNIQUE (comment_id, entry_id)
);

