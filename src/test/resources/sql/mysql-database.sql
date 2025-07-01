CREATE TABLE `blog` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(20),
    `user_id` INT,
    `tags` JSON,
    `attrs` JSON
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `user` (
    `id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(20),
    `age` SMALLINT
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

CREATE TABLE `rate` (
    `id` INT NOT NULL PRIMARY KEY,
    `level` SMALLINT
) ENGINE=INNODB CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

INSERT INTO `user` (name, age) VALUES ('user1', 18);
INSERT INTO `user` (name, age) VALUES ('user2', 20);
INSERT INTO `user` (name, age) VALUES ('user3', 34);

INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc1', 1, '[1, 2, 3]', '{"meta": "abc", "click": 1, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc2', 1, '[1, 2]', '{"meta": "bba", "click": 10, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc3', 1, '[2, 3]', '{"meta": "ccd", "click": 10, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc4', 2, '[1]', '{"meta": "abc", "click": 21, "reviewed": false}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc5', 2, '[2]', '{"meta": "bba", "click": 31, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc6', 2, '[3]', '{"meta": "abc", "click": 0, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc7', 2, '[1, 2, 3]', '{"meta": "bba", "click": 9, "reviewed": false}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc8', 2, '[1]', '{"meta": "ccd", "click": 1, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('abc9', 2, '[2]', '{"meta": "ccd", "click": 1, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('ebc0', 2, '[3]', '{"meta": "ccd", "click": 1, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('ddd6', 2, '[1, 2]', '{"meta": "abc", "click": 1, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('yc6', 2, '[1, 3]', '{"meta": "abc", "click": 1, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('xyz', 2, '[2, 3]', '{"meta": "abc", "click": 1, "reviewed": false}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES ('666', 3, '[3, 1]', '{"meta": "abc", "click": 1, "reviewed": true}');
INSERT INTO `blog` (name, user_id, tags, attrs) VALUES (null, 3, null, null);

INSERT INTO `rate` (`id`, `level`) VALUES (1, 5);
INSERT INTO `rate` (`id`, `level`) VALUES (2, 3);
INSERT INTO `rate` (`id`, `level`) VALUES (3, 1);
INSERT INTO `rate` (`id`, `level`) VALUES (4, 4);