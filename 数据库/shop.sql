/*
 Navicat Premium Data Transfer

 Source Server         : testPlay
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : shop

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 24/06/2025 20:16:54
*/
CREATE DATABASE IF NOT EXISTS shop CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE shop;

USE shop;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员用户名',
  `password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '管理员密码',
  `is_bool` tinyint(1) NULL DEFAULT 1 COMMENT '逻辑删除标志',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '管理员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES ('1', 'admin', '123456', 1);

-- ----------------------------
-- Table structure for buyer
-- ----------------------------
DROP TABLE IF EXISTS `buyer`;
CREATE TABLE `buyer`  (
  `buyer_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `buyer_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `buyer_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_bool` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`buyer_id`) USING BTREE,
  UNIQUE INDEX `buyer_name`(`buyer_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of buyer
-- ----------------------------
INSERT INTO `buyer` VALUES ('7bdf70c24a39401792cef08eab3c07ad', '蔡徐坤', '123456', 1);

-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
  `buyer_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `goods_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `num` int NOT NULL,
  `is_bool` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`buyer_id`, `goods_id`) USING BTREE,
  INDEX `fk_cart_goods`(`goods_id` ASC) USING BTREE,
  CONSTRAINT `fk_cart_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `buyer` (`buyer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_cart_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`goods_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `cart_chk_1` CHECK (`num` >= 0)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'G105ddf0c29a24a9d87b19d107a7adf63', 5, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'G556d53d582ed42c3a329cb53a4e589ee', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'G66e389f129e54c47a166df54e1179986', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'G7b85f93947ad4000b5570a163272ca98', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'G81037976f3e04fd78ab773c338effa96', 2, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'Gacc8c3fea66f40dd89da4e59694684e7', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'Gbd627ae53b7a49e4ad8cbb67104ff671', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'Gcc880e13eeeb45b9b8a957dfee1a7161', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'Gd720d708b01a4c7b9bde0212bd1ae8ed', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'Gec143f32f8a44917861ef4d00e5e3445', 1, 1);
INSERT INTO `cart` VALUES ('7bdf70c24a39401792cef08eab3c07ad', 'Gf6b967f2603e46f38790a1e72240d399', 1, 1);

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `goods_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `goods_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seller_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` double NOT NULL,
  `num` int NOT NULL,
  `images` json NULL,
  `is_bool` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`goods_id`) USING BTREE,
  INDEX `fk_goods_seller`(`seller_id` ASC) USING BTREE,
  CONSTRAINT `fk_goods_seller` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`seller_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `goods_chk_1` CHECK (`price` >= 0),
  CONSTRAINT `goods_chk_2` CHECK (`num` >= 0)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES ('G105ddf0c29a24a9d87b19d107a7adf63', 'aj8', '53315c3adce247df8d88e8ebd65376e1', '服装', 2050, 4000, '[\"http://localhost:8686/images/goods_8f28f0f6d4fd4b1fa5f89c621677731c.jpg\"]', 1);
INSERT INTO `goods` VALUES ('G556d53d582ed42c3a329cb53a4e589ee', 'aj1芝加哥', '53315c3adce247df8d88e8ebd65376e1', '服装', 2567, 4000, '[\"http://localhost:8686/images/goods_30ec332a4e114617a40f1315b78dc7a6.jpg\"]', 1);
INSERT INTO `goods` VALUES ('G66e389f129e54c47a166df54e1179986', '棒棒糖', '53315c3adce247df8d88e8ebd65376e1', '食品', 2, 4000, '[\"http://localhost:8686/images/goods_41fb7018987946f481263c22b443131d.jpg\"]', 1);
INSERT INTO `goods` VALUES ('G7b85f93947ad4000b5570a163272ca98', '菠萝雪糕', '53315c3adce247df8d88e8ebd65376e1', '食品', 3.5, 3977, '[\"http://localhost:8686/images/goods_f0cf7ddbbe6b4a979967fff694342354.jpg\"]', 1);
INSERT INTO `goods` VALUES ('G81037976f3e04fd78ab773c338effa96', '瑞克五代', '53315c3adce247df8d88e8ebd65376e1', '电子产品', 123.5, 3926, '[\"http://localhost:8686/images/goods_269209066572464bb00bee76b9f7953f.png\"]', 1);
INSERT INTO `goods` VALUES ('Gacc8c3fea66f40dd89da4e59694684e7', 'aj28', '53315c3adce247df8d88e8ebd65376e1', '服装', 5000, 3999, '[\"http://localhost:8686/images/goods_5f7b8d483ff144b3a7d30d920812eb5f.jpg\"]', 1);
INSERT INTO `goods` VALUES ('Gbd627ae53b7a49e4ad8cbb67104ff671', 'aj12', '53315c3adce247df8d88e8ebd65376e1', '服装', 1250, 4000, '[\"http://localhost:8686/images/goods_3be5d615acb94bd0ac50d32e91806c78.jpg\"]', 1);
INSERT INTO `goods` VALUES ('Gcc880e13eeeb45b9b8a957dfee1a7161', '耳机', '53315c3adce247df8d88e8ebd65376e1', '电子产品', 325, 4000, '[\"http://localhost:8686/images/goods_a41e7f097f944b3484c50e9dece42370.jpg\"]', 1);
INSERT INTO `goods` VALUES ('Gd720d708b01a4c7b9bde0212bd1ae8ed', '糖', '53315c3adce247df8d88e8ebd65376e1', '食品', 3, 4000, '[\"http://localhost:8686/images/goods_ffdf30d242f74a04b69e0e3c5fb8ecfe.jpg\"]', 1);
INSERT INTO `goods` VALUES ('Gec143f32f8a44917861ef4d00e5e3445', 'aj11', '53315c3adce247df8d88e8ebd65376e1', '服装', 1625, 4000, '[\"http://localhost:8686/images/goods_f90a20f3ed924702b0f9ac61a0b047ce.jpg\"]', 1);
INSERT INTO `goods` VALUES ('Gf6b967f2603e46f38790a1e72240d399', 'aj14最后一投', '53315c3adce247df8d88e8ebd65376e1', '服装', 6513, 3999, '[\"http://localhost:8686/images/goods_b43f5569dc924dcf8711431b7b3b71eb.jpg\"]', 1);

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `order_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `buyer_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seller_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `goods_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `state` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `num` int NOT NULL,
  `sum` double NOT NULL,
  `time` datetime NULL DEFAULT NULL,
  `is_bool` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`order_id`) USING BTREE,
  INDEX `fk_order_buyer`(`buyer_id` ASC) USING BTREE,
  INDEX `fk_order_seller`(`seller_id` ASC) USING BTREE,
  INDEX `fk_order_goods`(`goods_id` ASC) USING BTREE,
  CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `buyer` (`buyer_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`goods_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `seller` (`seller_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `order_chk_1` CHECK (`num` >= 0),
  CONSTRAINT `order_chk_2` CHECK (`sum` >= 0)
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES ('0e57cd816a274dab8bf27918d3ee31a3', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'Gf6b967f2603e46f38790a1e72240d399', '支付成功', 1, 6513, '2025-06-24 19:56:45', 1);
INSERT INTO `order` VALUES ('1e06cc9413d043358644d57cf738ca7b', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G7b85f93947ad4000b5570a163272ca98', '支付成功', 8, 28, '2025-06-23 13:53:06', 1);
INSERT INTO `order` VALUES ('22f2c5922c9041b5b17c870001f04680', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G7b85f93947ad4000b5570a163272ca98', '支付成功', 3, 10.5, '2025-06-23 13:45:47', 1);
INSERT INTO `order` VALUES ('288fa7a115cd45e58e99ed28a7d78ae1', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G7b85f93947ad4000b5570a163272ca98', '支付成功', 5, 17.5, '2025-06-23 14:00:23', 1);
INSERT INTO `order` VALUES ('4d3effd3fbdb4ae1afab17798ad41f0d', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G105ddf0c29a24a9d87b19d107a7adf63', '取消', 1, 2050, '2025-06-24 19:57:33', 1);
INSERT INTO `order` VALUES ('537fefe944d54a8098ca35228f9fe6f5', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G81037976f3e04fd78ab773c338effa96', '支付成功', 1, 123.5, '2025-06-23 13:26:53', 1);
INSERT INTO `order` VALUES ('5f4dd6d8bbb84335a9076ca11c9964c7', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G7b85f93947ad4000b5570a163272ca98', '支付成功', 7, 24.5, '2025-06-23 13:44:40', 1);
INSERT INTO `order` VALUES ('73cf912249774feb82c7cf2af7ae53cf', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'Gf6b967f2603e46f38790a1e72240d399', '取消', 1, 6513, '2025-06-24 15:54:26', 1);
INSERT INTO `order` VALUES ('7cd01d362313445291d5b8bfa1b5c488', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'Gacc8c3fea66f40dd89da4e59694684e7', '支付成功', 1, 5000, '2025-06-24 19:42:28', 1);
INSERT INTO `order` VALUES ('90b0cc04d7f345239d2712940d9d2573', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G105ddf0c29a24a9d87b19d107a7adf63', '取消', 5, 10250, '2025-06-24 17:15:22', 1);
INSERT INTO `order` VALUES ('985fa56d26a948d89469c4064203cea3', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G81037976f3e04fd78ab773c338effa96', '支付成功', 4, 494, '2025-06-23 14:00:23', 1);
INSERT INTO `order` VALUES ('a76f2dd5a5ef4171a17eccbd968ef41c', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G81037976f3e04fd78ab773c338effa96', '取消', 7, 864.5, '2025-06-23 11:11:02', 1);
INSERT INTO `order` VALUES ('dd3ded63e7044c4b86eb64b582d6264f', '7bdf70c24a39401792cef08eab3c07ad', '53315c3adce247df8d88e8ebd65376e1', 'G81037976f3e04fd78ab773c338effa96', '支付成功', 6, 741, '2025-06-23 13:53:06', 1);

-- ----------------------------
-- Table structure for seller
-- ----------------------------
DROP TABLE IF EXISTS `seller`;
CREATE TABLE `seller`  (
  `seller_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seller_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `seller_password` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_bool` tinyint(1) NULL DEFAULT 1,
  PRIMARY KEY (`seller_id`) USING BTREE,
  UNIQUE INDEX `seller_name`(`seller_name` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of seller
-- ----------------------------
INSERT INTO `seller` VALUES ('53315c3adce247df8d88e8ebd65376e1', '丁真', '123456', 1);
INSERT INTO `seller` VALUES ('a8f17c4e7c4947d6983bc7371bbb0f25', '吴亦凡', '123456', 1);

SET FOREIGN_KEY_CHECKS = 1;
