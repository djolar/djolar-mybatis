CREATE TABLE `blog` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(20),
    `user_id` INT
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(20),
    `age` SMALLINT
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `user` (name, age) VALUES ('user1', 18);
INSERT INTO `user` (name, age) VALUES ('user2', 20);
INSERT INTO `user` (name, age) VALUES ('user3', 34);

INSERT INTO `blog` (name, user_id) VALUES ('abc1', 1);
INSERT INTO `blog` (name, user_id) VALUES ('abc2', 1);
INSERT INTO `blog` (name, user_id) VALUES ('abc3', 1);
INSERT INTO `blog` (name, user_id) VALUES ('abc4', 2);
INSERT INTO `blog` (name, user_id) VALUES ('abc5', 2);
INSERT INTO `blog` (name, user_id) VALUES ('abc6', 2);
INSERT INTO `blog` (name, user_id) VALUES ('abc7', 2);
INSERT INTO `blog` (name, user_id) VALUES ('abc8', 2);
INSERT INTO `blog` (name, user_id) VALUES ('abc9', 2);
INSERT INTO `blog` (name, user_id) VALUES ('ebc0', 2);
INSERT INTO `blog` (name, user_id) VALUES ('ddd6', 2);
INSERT INTO `blog` (name, user_id) VALUES ('yc6', 2);
INSERT INTO `blog` (name, user_id) VALUES ('xyz', 2);
INSERT INTO `blog` (name, user_id) VALUES ('666', 3);