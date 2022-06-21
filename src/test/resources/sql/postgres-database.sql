CREATE TABLE "blog" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(20),
    "user_id" INT
);

CREATE TABLE "user" (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(20),
    "age" SMALLINT
);

INSERT INTO "user" (name, age) VALUES ('user1', 18);
INSERT INTO "user" (name, age) VALUES ('user2', 20);
INSERT INTO "user" (name, age) VALUES ('user3', 34);

INSERT INTO "blog" (name, user_id) VALUES ('abc1', 1);
INSERT INTO "blog" (name, user_id) VALUES ('abc2', 1);
INSERT INTO "blog" (name, user_id) VALUES ('abc3', 1);
INSERT INTO "blog" (name, user_id) VALUES ('abc4', 2);
INSERT INTO "blog" (name, user_id) VALUES ('abc5', 2);
INSERT INTO "blog" (name, user_id) VALUES ('abc6', 2);
INSERT INTO "blog" (name, user_id) VALUES ('abc7', 2);
INSERT INTO "blog" (name, user_id) VALUES ('abc8', 2);
INSERT INTO "blog" (name, user_id) VALUES ('abc9', 2);
INSERT INTO "blog" (name, user_id) VALUES ('ebc0', 2);
INSERT INTO "blog" (name, user_id) VALUES ('ddd6', 2);
INSERT INTO "blog" (name, user_id) VALUES ('yc6', 2);
INSERT INTO "blog" (name, user_id) VALUES ('xyz', 2);
INSERT INTO "blog" (name, user_id) VALUES ('666', 3);
INSERT INTO "blog" (name, user_id) VALUES (null, 3);
