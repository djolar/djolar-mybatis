CREATE TABLE `blog` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(20),
    `user_id` INT,
    `tags` JSON
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(20),
    `age` SMALLINT
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `user` (name, age) VALUES ('user1', 18);
INSERT INTO `user` (name, age) VALUES ('user2', 20);
INSERT INTO `user` (name, age) VALUES ('user3', 34);

INSERT INTO `blog` (name, user_id, tags) VALUES ('abc1', 1, '[1, 2, 3]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc2', 1, '[1, 2]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc3', 1, '[2, 3]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc4', 2, '[1]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc5', 2, '[2]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc6', 2, '[3]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc7', 2, '[1, 2, 3]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc8', 2, '[1]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('abc9', 2, '[2]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('ebc0', 2, '[3]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('ddd6', 2, '[1, 2]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('yc6', 2, '[1, 3]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('xyz', 2, '[2, 3]');
INSERT INTO `blog` (name, user_id, tags) VALUES ('666', 3, '[3, 1]');
INSERT INTO `blog` (name, user_id, tags) VALUES (null, 3, null);