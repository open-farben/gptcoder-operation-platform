# GPTCoder运营平台
<hr>
## 总体设计描述

框架采用springboot3、gredle8、mybatis-plus3、java17等来实现。采用模块化的方式来实现，具体功能为一个一个的模块。服务引入对应的模块即可。带service名称的模块即为一个服务。

## 模块说明

|                   模块名称                   | 描述                                     |
|:----------------------------------------:|----------------------------------------|
|               commons-ddd                | ddd设计相关的通用接口和工具类等                      |
|            commons-errorcode             | 统一系统错误码                                |
|               commons-file               | 文件处理通用模块、文件上传、下载、删除等                   |
|               commons-mvc                | mvc相关的通用组件。包括全局异常处理、拦截器、返回数据包装等        |
|              commons-redis               | redis通用模块                              |
|              plugin-version              | 插件版本管理模块：管理idea和vscode的插件版本，包括版本发布、下线等 |
|                statistics                | 统计模块：主要是插件的使用统计                        |
|                   user                   | 用户模块：用户分为插件用户和平台用户                     |
|   gptcoder-operation-platform-service    | 没有具体业务逻辑，为服务模块，引入其它业务功能模块，形成一个服务       |
|     gptcoder-authentication-service      | 统一认证模块，用于用户登录                          |

## 主要包说明

| 包名称                            | 描述                                                         |
| --------------------------------- | ------------------------------------------------------------ |
| facade                            | 对外接口层：提供对外的接口，给第三方或前端调用               |
| application.service               | 应用层服务层：做一些业务不强相关的校验、然后调用领域层或仓储层，事务控制主要是在这一层 |
| command                           | request bode的参数以及参数校验                               |
| config                            | 用于放置一些配置类                                           |
| domain                            | 领域层：除了查询外的业务强相关的实现都在该包下               |
| domain.entity                     | 领域层的实体定义                                             |
| domain.event                      | 领域层的事件定义                                             |
| dto                               | 定义业务要返回的数据                                         |
| exception                         | 定义业务异常                                                 |
| infrastructure                    | 基础设施层：存放仓储层、模块常量、工具等                     |
| infrastructure.repository         | 仓储层：用于数据的存储和查询                                 |
| infrastructure.repository.facade  | 仓储层对内接口，提供存储和查询接口给上层用                   |
| infrastructure.repository.mapper  | 对应mybatis-plus的mapper层                                   |
| infrastructure.repository.po      | 定义表结构                                                   |
| infrastructure.repository.service | 对应mybatis-plus的service层                                  |

## module-info.java

由于是模块化开发，因此除了带service字样的服务模块，其它功能模块都会有这个文件。需要在该文件定义该模块导出和依赖的模块信息。除了在build.gradle中引入需要的组件外，还需要在这里面引入需要的模块。

## 部署

请参考[部署说明](docker-compose/部署说明.md)。