/*
 Navicat Premium Data Transfer

 Source Server         : 本机连接
 Source Server Type    : MySQL
 Source Server Version : 80017
 Source Host           : localhost:3306
 Source Schema         : interview

 Target Server Type    : MySQL
 Target Server Version : 80017
 File Encoding         : 65001

 Date: 09/11/2022 14:40:28
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for tb_cash_out_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_cash_out_order`;
CREATE TABLE `tb_cash_out_order`  (
  `order_id` bigint(20) NOT NULL COMMENT '提现订单id',
  `user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `cash_out_amount` int(11) NOT NULL COMMENT '提现金额',
  `create_time` timestamp(0) NOT NULL COMMENT '创建时间',
  `end_time` timestamp(0) NULL DEFAULT NULL COMMENT '结束时间',
  `is_success` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '提现是否到账：0 未到账，1 已到账',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_purchase_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_purchase_order`;
CREATE TABLE `tb_purchase_order`  (
  `order_id` bigint(20) NOT NULL COMMENT '购买订单id',
  `user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `purchase_amount` int(11) NOT NULL COMMENT '购买金额',
  `create_time` timestamp(0) NOT NULL COMMENT '订单开始时间',
  `finish_time` timestamp(0) NOT NULL COMMENT '若订单完成，则为完成时间，否为过期时间',
  `is_finish` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单是否完成：0：否 1：完成',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '购买订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_recharge_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_recharge_order`;
CREATE TABLE `tb_recharge_order`  (
  `order_id` bigint(20) NOT NULL COMMENT '充值订单id',
  `user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `recharge_amount` int(11) NOT NULL COMMENT '充值金额',
  `create_time` timestamp(0) NOT NULL COMMENT '订单开始时间',
  `finish_time` timestamp(0) NOT NULL COMMENT '若订单完成，则为完成时间，否为过期时间',
  `is_finish` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '订单是否完成：0：否 1：完成',
  PRIMARY KEY (`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '充值订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tb_refund_order
-- ----------------------------
DROP TABLE IF EXISTS `tb_refund_order`;
CREATE TABLE `tb_refund_order`  (
  `refund_order_id` bigint(20) NOT NULL COMMENT '退款订单id',
  `user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `refund_amount` int(11) NOT NULL COMMENT '退款金额',
  `order_id` bigint(20) NOT NULL COMMENT '退款的关联订单id',
  `create_time` timestamp(0) NOT NULL COMMENT '订单开始时间',
  `end_time` timestamp(0) NULL DEFAULT NULL COMMENT '结束时间',
  `is_success` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '退款是否完成：0 未完成，1 已取消，2 已完成退款',
  PRIMARY KEY (`refund_order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '退款订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_wallet
-- ----------------------------
DROP TABLE IF EXISTS `user_wallet`;
CREATE TABLE `user_wallet`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `wallet_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '钱包id',
  `balance` int(10) UNSIGNED ZEROFILL NOT NULL COMMENT '余额',
  `update_time` timestamp(0) NOT NULL COMMENT '余额最后变动时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户钱包表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for wallet_balance_record
-- ----------------------------
DROP TABLE IF EXISTS `wallet_balance_record`;
CREATE TABLE `wallet_balance_record`  (
  `user_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `wallet_id` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '钱包id',
  `balance_change` int(11) NOT NULL COMMENT '余额变动数（单位：元）',
  `is_entry` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '余额是增还是减：0：减  1：增',
  `order_id` bigint(20) NOT NULL COMMENT '关联的订单id',
  `create_time` timestamp(0) NOT NULL COMMENT '创建时间'
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '钱包余额变动记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
