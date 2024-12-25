// 引入quartz，模块化会出错
//module gptcoder.operation.platform.statistics {
//    exports cn.com.farben.gptcoder.operation.platform.statistics.facade;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.command;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.application.service;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.application.component;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.config;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.enums;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.mapper;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.service;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.impl;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.service.impl;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.dto;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.domain.entity;
//    exports cn.com.farben.gptcoder.operation.platform.statistics.handler;
//
//    opens cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.po to com.baomidou.mybatis.plus.core;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.service.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.application.service to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.application.component to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.config to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.facade to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.command;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.domain.entity;
//    opens cn.com.farben.gptcoder.operation.platform.statistics.dto;
//
//    requires gptcoder.operation.platform.user;
//
//    requires spring.core;
//    requires spring.web;
//    requires spring.context;
//    requires spring.boot;
//    requires spring.beans;
//    requires spring.aop;
//    requires spring.data.redis;
//    requires spring.tx;
//
//    requires spring.boot.starter.data.redis;
//
//    requires cn.hutool.poi;
//    requires cn.hutool.log;
//    requires cn.hutool.json;
//    requires cn.hutool.core;
//
//    requires com.fasterxml.jackson.annotation;
//    requires lombok;
//    requires org.slf4j;
//    requires com.baomidou.mybatis.plus.extension;
//    requires com.baomidou.mybatis.plus.core;
//    requires com.baomidou.mybatis.plus.annotation;
//    requires jakarta.validation;
//    requires jakarta.servlet;
//    requires org.mybatis;
//    requires spring.data.commons;
//    requires org.apache.poi.poi;
//    requires spring.integration.redis;
//
//    requires farben.commons.ddd;
//    requires farben.commons.web;
//    requires gptcoder.operation.platform.commons.user;
//    requires gptcoder.operation.platform.commons.model;
//    requires org.mybatis.spring;
//    requires gptcoder.operation.platform.dictionary;
//    requires quartz;
//    requires spring.context.support;
//}
