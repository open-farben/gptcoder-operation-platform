# 1、安装Apisix
参考链接
```shell
https://apisix.apache.org/zh/docs/apisix/installation-guide/
```
```shell
git clone https://github.com/apache/apisix-docker.git
cd apisix-docker/example
mkdir dashboard_conf
vi conf.yaml
cd apisix-docker/example/apisix_conf
#添加dashboard.yaml内容在当前目录下
#内容在当前目录的conf.yaml

#修改docker-compose.yaml,service增加dashboard
  apisix-dashboard:
    image: apache/apisix-dashboard:latest
    restart: always
    volumes:
    - ./dashboard_conf/conf.yaml:/usr/local/apisix-dashboard/conf/conf.yaml
    ports:
    - "9000:9000"
    environment:
      - TZ=Asia/Shanghai
    networks:
      apisix:
      
#修改完成后启动apisix
cd apisix-docker/example
docker-compose -p docker-apisix up -d
```
# 2、配置Apisix
```shell
访问localhost或者部署的机器ip，端口为9000
http://localhost:9000/
#账号密码
admin/admin
```

## 2.1 配置路由

### 2.1.1 配置前端路由
![img.png](images/img333.png)
![img_1.png](images/img_1.png)
![img_2.png](images/img_2.png)
![img.png](images/img.png)

### 2.1.2 配置根路径路由
![img_3.png](images/img_3.png)
![img_4.png](images/img_4.png)
![img_5.png](images/img_5.png)

### 2.1.3 配置智能代理路由
![img_6.png](images/img_6.png)
![img_7.png](images/img_7.png)
![img_8.png](images/img_8.png)
![img_9.png](images/img_9.png)

### 2.1.4 配置llm模型路由
![img_10.png](images/img_10.png)
![img_11.png](images/img_11.png)
![img_12.png](images/img_12.png)
![img_13.png](images/img_13.png)

### 2.1.5 配置管理平台路由
![img_14.png](images/img_14.png)
![img_15.png](images/img_15.png)
![img_16.png](images/img_16.png)






