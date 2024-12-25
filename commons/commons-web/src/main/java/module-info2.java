//module farben.commons.web {
//    requires cn.hutool.log;
//    requires cn.hutool.core;
//    requires cn.hutool.crypto;
//    requires cn.hutool.jwt;
//
//    requires jakarta.validation;
//    requires jakarta.servlet;
//
//    requires spring.core;
//    requires spring.web;
//    requires spring.context;
//    requires spring.webmvc;
//    requires spring.beans;
//    requires spring.webflux;
//
//    requires java.sql;
//    requires static lombok;
//    requires io.netty.transport;
//    requires io.netty.handler;
//    requires reactor.netty.http;
//    requires reactor.netty.core;
//    requires com.fasterxml.jackson.datatype.jsr310;
//    requires spring.boot.autoconfigure;
//    requires spring.boot;
//    requires com.fasterxml.jackson.databind;
//
//    exports cn.com.farben.commons.web;
//    exports cn.com.farben.commons.web.enums;
//    exports cn.com.farben.commons.web.exception;
//    exports cn.com.farben.commons.web.handler;
//    exports cn.com.farben.commons.web.utils;
//    exports cn.com.farben.commons.web.constants;
//    exports cn.com.farben.commons.web.bo;
//    exports cn.com.farben.commons.web.config;
//}