//module gptcoder.operation.platform.plugin {
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.facade;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.command;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.config;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.application.service;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.enums;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.po;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.mapper;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.service;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.impl;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.service.impl;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.dto;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.domain.entity;
//    exports cn.com.farben.gptcoder.operation.platform.plugin.version.domain.event;
//
//    opens cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.po to com.baomidou.mybatis.plus.core;
//    opens cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.service.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.plugin.version.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.plugin.version.application.service to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.plugin.version.facade to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.plugin.version.config to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.plugin.version.command;
//
//    requires spring.core;
//    requires spring.web;
//    requires spring.context;
//    requires spring.boot;
//    requires spring.beans;
//    requires spring.aop;
//    requires spring.webmvc;
//    requires spring.data.redis;
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
//    requires cn.hutool.core;
//    requires cn.hutool.log;
//    requires cn.hutool.json;
//    requires cn.hutool.system;
//    requires java.xml;
//
//    requires gptcoder.operation.platform.commons.user;
//
//    requires farben.commons.ddd;
//    requires farben.commons.web;
//    requires farben.commons.file;
//    requires org.mybatis.spring;
//}
