//module farben.commons.file {
//    exports cn.com.farben.commons.file.exception;
//    exports cn.com.farben.commons.file.infrastructure;
//    exports cn.com.farben.commons.file.infrastructure.repository.facade;
//    exports cn.com.farben.commons.file.infrastructure.repository.facade.impl;
//    exports cn.com.farben.commons.file.infrastructure.repository.mapper;
//    exports cn.com.farben.commons.file.infrastructure.repository.po;
//    exports cn.com.farben.commons.file.infrastructure.repository.service;
//    exports cn.com.farben.commons.file.infrastructure.repository.service.impl;
//    exports cn.com.farben.commons.file.domain;
//    exports cn.com.farben.commons.file.domain.entity;
//    exports cn.com.farben.commons.file.domain.event;
//    exports cn.com.farben.commons.file.infrastructure.enums;
//    exports cn.com.farben.commons.file.facade;
//    exports cn.com.farben.commons.file.command;
//    exports cn.com.farben.commons.file.dto;
//    exports cn.com.farben.commons.file.vo;
//    exports cn.com.farben.commons.file.application.service;
//    exports cn.com.farben.commons.file.job;
//    exports cn.com.farben.commons.file.config;
//    exports cn.com.farben.commons.file.infrastructure.tools;
//
//    opens cn.com.farben.commons.file.infrastructure.repository.po to com.baomidou.mybatis.plus.core;
//    opens cn.com.farben.commons.file.infrastructure.repository.service.impl to spring.core;
//    opens cn.com.farben.commons.file.infrastructure.repository.facade.impl to spring.core;
//    opens cn.com.farben.commons.file.application.service to spring.core;
//    opens cn.com.farben.commons.file.facade to spring.core;
//    opens cn.com.farben.commons.file.job to spring.core;
//    opens cn.com.farben.commons.file.command;
//    opens cn.com.farben.commons.file.infrastructure.enums;
//
//    requires com.baomidou.mybatis.plus.extension;
//    requires com.baomidou.mybatis.plus.core;
//    requires com.baomidou.mybatis.plus.annotation;
//
//    requires org.mybatis.spring;
//    requires org.mybatis;
//
//    requires cn.hutool.log;
//    requires cn.hutool.core;
//    requires cn.hutool.system;
//    requires cn.hutool.extra;
//    requires cn.hutool.json;
//
//    requires com.fasterxml.jackson.annotation;
//
//    requires jakarta.servlet;
//    requires jakarta.validation;
//    requires jakarta.annotation;
//
//    requires spring.context;
//    requires spring.beans;
//    requires spring.web;
//    requires spring.core;
//    requires spring.boot;
//    requires spring.tx;
//    requires spring.data.redis;
//
//    requires lombok;
//
//    requires farben.commons.ddd;
//    requires farben.commons.web;
//}