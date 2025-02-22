CREATE TABLE "blog" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(20),
    "user_id" INT,
    "tags" JSONB
);

CREATE TABLE "user" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(20),
    "age" SMALLINT
);

CREATE TABLE "rate" (
    "id" INT PRIMARY KEY,
    "level" SMALLINT
);

INSERT INTO "user" (name, age) VALUES ('user1', 18);
INSERT INTO "user" (name, age) VALUES ('user2', 20);
INSERT INTO "user" (name, age) VALUES ('user3', 34);

INSERT INTO "blog" (name, user_id, tags) VALUES ('abc1', 1, '[1, 2, 3]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc2', 1, '[1, 2]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc3', 1, '[2, 3]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc4', 2, '[1]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc5', 2, '[2]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc6', 2, '[3]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc7', 2, '[1, 2, 3]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc8', 2, '[1]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('abc9', 2, '[2]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('ebc0', 2, '[3]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('ddd6', 2, '[1, 2]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('yc6', 2, '[1, 3]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('xyz', 2, '[2, 3]');
INSERT INTO "blog" (name, user_id, tags) VALUES ('666', 3, '[3, 1]');
INSERT INTO "blog" (name, user_id, tags) VALUES (null, 3, null);

INSERT INTO "rate" ("id", "level") VALUES (1, 5);
INSERT INTO "rate" ("id", "level") VALUES (2, 3);
INSERT INTO "rate" ("id", "level") VALUES (3, 1);
INSERT INTO "rate" ("id", "level") VALUES (4, 4);