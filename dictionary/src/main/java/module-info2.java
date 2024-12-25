//module gptcoder.operation.platform.dictionary {
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.facade;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.command;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.config;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.application.service;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.application.component;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.mapper;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.service;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.impl;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.service.impl;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.utils;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.formater;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.domain.entity;
//
//    opens cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.po to com.baomidou.mybatis.plus.core;
//    opens cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.service.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.dictionary.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.dictionary.application.service to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.dictionary.facade to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.dictionary.config to spring.core;
//    opens cn.com.farben.gptcoder.operation.platform.dictionary.command;
//    exports cn.com.farben.gptcoder.operation.platform.dictionary.domain;
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
//    requires org.mybatis;
//    requires com.google.common;
//    requires org.apache.commons.lang3;
//
//    requires cn.hutool.core;
//    requires cn.hutool.log;
//    requires cn.hutool.json;
//
//    requires gptcoder.operation.platform.commons.user;
//
//    requires farben.commons.ddd;
//    requires farben.commons.web;
//    requires org.mybatis.spring;
//    requires com.fasterxml.jackson.databind;
//    requires com.fasterxml.jackson.datatype.jsr310;
//    requires spring.boot.autoconfigure;
//}
