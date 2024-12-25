// 引入quartz，模块化会失败
//module gptcoder.operation.platform.user {
//    exports cn.com.farben.gptcoder.operation.platform.user.facade;
//    exports cn.com.farben.gptcoder.operation.platform.user.command;
//    exports cn.com.farben.gptcoder.operation.platform.user.command.organ;
//    exports cn.com.farben.gptcoder.operation.platform.user.command.excel;
//    exports cn.com.farben.gptcoder.operation.platform.user.command.role;
//    exports cn.com.farben.gptcoder.operation.platform.user.application.service;
//    exports cn.com.farben.gptcoder.operation.platform.user.application.component;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.mapper;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.impl;
//    exports cn.com.farben.gptcoder.operation.platform.user.dto;
//    exports cn.com.farben.gptcoder.operation.platform.user.domain.entity;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.utils to spring.beans, gptcoder.operation.platform.dictionary;
//    exports cn.com.farben.gptcoder.operation.platform.user.config;
//    exports cn.com.farben.gptcoder.operation.platform.user.domain.event;
//    exports cn.com.farben.gptcoder.operation.platform.user.infrastructure.enums;
//    exports cn.com.farben.gptcoder.operation.platform.user.dto.role;
//    exports cn.com.farben.gptcoder.operation.platform.user.exchange;
//
//    exports cn.com.farben.gptcoder.operation.platform.group.facade;
//    exports cn.com.farben.gptcoder.operation.platform.group.application.service;
//    exports cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po;
//    exports cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.mapper;
//    exports cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.service;
//    exports cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.service.impl;
//    exports cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade;
//    exports cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.impl;
//    exports cn.com.farben.gptcoder.operation.platform.group.dto;
//    exports cn.com.farben.gptcoder.operation.platform.group.domain;
//    exports cn.com.farben.gptcoder.operation.platform.group.domain.event;
//    exports cn.com.farben.gptcoder.operation.platform.group.domain.entity;
//    exports cn.com.farben.gptcoder.operation.platform.group.command;
//
//    opens cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.po to com.baomidou.mybatis.plus.core, com.fasterxml.jackson.databind;
//    opens cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.user.application.service to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.user.application.component to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.user.facade to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.user.infrastructure.utils to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.user.config to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.user.util;
//    opens cn.com.farben.gptcoder.operation.platform.user.command;
//    opens cn.com.farben.gptcoder.operation.platform.user.command.excel;
//    opens cn.com.farben.gptcoder.operation.platform.user.command.role;
//    opens cn.com.farben.gptcoder.operation.platform.user.command.knowledge;
//    opens cn.com.farben.gptcoder.operation.platform.user.job;
//    opens cn.com.farben.gptcoder.operation.platform.user.infrastructure.repository.service.impl;
//
//    opens cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.po to com.baomidou.mybatis.plus.core;
//    opens cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.service.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.group.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.group.facade to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.group.command;
//    opens cn.com.farben.gptcoder.operation.platform.group.application.service to spring.core;
//
//    requires gptcoder.operation.platform.commons.auth;
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
//    requires com.google.common;
//
//    requires cn.hutool.jwt;
//    requires cn.hutool.crypto;
//    requires cn.hutool.core;
//    requires cn.hutool.log;
//    requires cn.hutool.json;
//    requires cn.hutool.poi;
//    requires org.apache.poi.ooxml;
//    requires gptcoder.operation.platform.plugin;
//    requires cn.hutool.http;
//    requires spring.webflux;
//    requires reactor.core;
//    requires org.eclipse.jgit;
//    requires redisson;
//    requires org.apache.commons.io;
//    requires com.fasterxml.jackson.core;
//    requires jakarta.annotation;
//    requires micrometer.commons;
//    requires com.esotericsoftware.kryo;
//    requires java.logging;
//    requires reactor.netty.http;
//    requires io.netty.transport;
//    requires io.netty.handler;
//    requires reactor.netty.core;
//
//    requires farben.commons.web;
//    requires farben.commons.ddd;
//    requires gptcoder.operation.platform.commons.user;
//    requires farben.commons.file;
//    requires org.mybatis.spring;
//    requires gptcoder.operation.platform.dictionary;
//    requires com.fasterxml.jackson.databind;
//    requires org.apache.commons.collections4;
//    requires org.hibernate.validator;
//}
