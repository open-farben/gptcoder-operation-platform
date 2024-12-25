//module gptcoder.operation.platform.model {
//    exports cn.com.farben.gptcoder.operation.platform.model.facade;
//    exports cn.com.farben.gptcoder.operation.platform.model.command;
//    exports cn.com.farben.gptcoder.operation.platform.model.application.service;
//    exports cn.com.farben.gptcoder.operation.platform.model.infrastructure.enums;
//    exports cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po;
//    exports cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.mapper;
//    exports cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.service;
//    exports cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade;
//    exports cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.impl;
//    exports cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.service.impl;
//    exports cn.com.farben.gptcoder.operation.platform.model.dto;
//    exports cn.com.farben.gptcoder.operation.platform.model.config;
//    exports cn.com.farben.gptcoder.operation.platform.model.domain.entity;
//    exports cn.com.farben.gptcoder.operation.platform.model.domain.event;
//
//    opens cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.po to com.baomidou.mybatis.plus.core;
//    opens cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.service.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.model.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.model.application.service to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.model.facade to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.model.command;
//
//    requires spring.core;
//    requires spring.web;
//    requires spring.context;
//    requires spring.boot;
//    requires spring.beans;
//    requires spring.aop;
//    requires spring.data.redis;
//    requires spring.webmvc;
//    requires spring.tx;
//
//    requires com.fasterxml.jackson.annotation;
//    requires static lombok;
//    requires org.slf4j;
//    requires com.baomidou.mybatis.plus.extension;
//    requires com.baomidou.mybatis.plus.core;
//    requires com.baomidou.mybatis.plus.annotation;
//    requires jakarta.validation;
//    requires jakarta.servlet;
//    requires org.apache.commons.lang3;
//    requires org.mybatis;
//
//    requires cn.hutool.jwt;
//    requires cn.hutool.core;
//    requires cn.hutool.log;
//    requires cn.hutool.json;
//
//    requires gptcoder.operation.platform.commons.model;
//    requires farben.commons.ddd;
//    requires farben.commons.web;
//    requires org.mybatis.spring;
//}
