//module gptcoder.operation.platform.commons.auth {
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure.utils;
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.service.impl;
//    exports cn.com.farben.gptcoder.operation.commons.auth.application.service;
//    exports cn.com.farben.gptcoder.operation.commons.auth.facade to spring.beans,spring.web,spring.aop;
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade.impl;
//    exports cn.com.farben.gptcoder.operation.commons.auth.dto;
//    exports cn.com.farben.gptcoder.operation.commons.auth.command;
//    exports cn.com.farben.gptcoder.operation.commons.auth.config;
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.po;
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure.enums;
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.service;
//    exports cn.com.farben.gptcoder.operation.commons.auth.domain.entity;
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade;
//
//    opens cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.po to com.baomidou.mybatis.plus.core;
//    opens cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.service.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.commons.auth.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.gptcoder.operation.commons.auth.facade to spring.core;
//    opens cn.com.farben.gptcoder.operation.commons.auth.command;
//    opens cn.com.farben.gptcoder.operation.commons.auth.application.service to spring.core;
//    exports cn.com.farben.gptcoder.operation.commons.auth.infrastructure;
//
//    requires com.fasterxml.jackson.annotation;
//
//    requires cn.hutool.core;
//    requires cn.hutool.crypto;
//    requires cn.hutool.system;
//    requires cn.hutool.json;
//    requires cn.hutool.log;
//    requires cn.hutool.extra;
//
//    requires static lombok;
//    requires spring.context;
//    requires spring.web;
//    requires jakarta.validation;
//    requires com.baomidou.mybatis.plus.annotation;
//    requires com.baomidou.mybatis.plus.core;
//    requires com.baomidou.mybatis.plus.extension;
//    requires jakarta.servlet;
//    requires org.mybatis;
//    requires spring.tx;
//    requires farben.commons.web;
//    requires farben.commons.ddd;
//    requires gptcoder.operation.platform.commons.user;
//    requires org.mybatis.spring;
//}