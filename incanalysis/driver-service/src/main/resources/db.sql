SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;
set @@GLOBAL.sql_mode='STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

DROP TABLE IF EXISTS `repository`;
CREATE TABLE `repository`(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `description` varchar(4095) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `create_time` datetime(0) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `repository` VALUES (1,"我是仓库1","我是1号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (2,"我是仓库2","我是2号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (3,"我是仓库3","我是3号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (4,"我是仓库4","我是4号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (5,"我是仓库5","我是5号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (6,"我是仓库6","我是6号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (7,"我是仓库7","我是7号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (8,"我是仓库8","我是8号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (9,"我是仓库9","我是9号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (10,"我是仓库10","我是10号仓库",'2022-02-28 10:00:00');
INSERT INTO `repository` VALUES (11,"我是仓库11","我是11号仓库",'2022-02-28 10:00:00');

DROP TABLE IF EXISTS `file`;
CREATE TABLE `file` (
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `repository_id` int(11) NOT NULL,
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `resource_dir` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
    `is_changed` BOOL NOT NULL DEFAULT 0,
    `create_time` datetime(0) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `fk_repository_file`(`repository_id`) USING BTREE,
    CONSTRAINT `fk_repository_file` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `file` VALUES (1,1,"core","py","file/1/core.py",1,'2022-02-28 10:00:00');

DROP TABLE IF EXISTS `dependency`;
CREATE TABLE `dependency`(
    `id` int(11) NOT NULL AUTO_INCREMENT,
    `repository_id` int(11) NOT NULL,
    `file_id` int(11) NOT NULL,
    `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
    `use_count` int(11) NOT NULL,
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `fk_repository_dependency`(`repository_id`) USING BTREE,
    INDEX `fk_file_dependency`(`file_id`) USING BTREE,
    CONSTRAINT `fk_repository_dependency` FOREIGN KEY (`repository_id`) REFERENCES `repository` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_file_dependency` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;