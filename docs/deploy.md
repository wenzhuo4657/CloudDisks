# Ubuntu部署





## 前端部署

` sudo vim /etc/nginx/nginx.conf`



```nginx.conf

     server {
                listen 80;
                charset utf-8;
                location / {
                        alias  /cloudDisks/front/dist/;
                        try_files $uri  $uri/  /index.html;
                        index  index.html  index.htm;
                }

                location /api {
                        proxy_pass  http://localhost:7090/api;
                        proxy_set_header   x-forwarded-for  $remote_addr;
                }
        }

```



1， ubuntu等系统通过apt、yum等软件下载器安装的nginx，需要注释掉默引用的配置文件，否则可能会由于覆盖导致配置不生效。 

#include /etc/nginx/conf.d/*.conf;





2，目录权限不足

nginx.conf默认配置使用用户 www-data;，该用户一般自动创建，可通过`getent  passwd  www-data `查询基本信息。



```
root@ser231760304132:/cloudDisks# getent  passwd  www-data 
www-data:x:33:33:www-data:/var/www:/usr/sbin/nologin

```

上述信息表明家目录在/var/www，但是该目录的权限在某些情况下会是属于root:root,此时我们的解决办法有两种

- 1，改变用户www-data,使其加入root组

- 2，改变目录所有者和所有组
  -   chown   -R  www-data:www-data  /cloudDisks/       -R表示递归改变

- 3，修改nginx进程的启动用户，更改root





对于1、3通常不推荐，因为www-data是大部分的前端程序的通用用户，这两种做法都会使其拥有过多的权限。





## 组件部署



部署mysql、redis，然后将sql文件导入即可，







## 后端部署





使用插件spring-boot-maven-plugin打包springboot程序。

```

        <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <mainClass>cn.wenzhuo4657.Main</mainClass>
                    <layout>JAR</layout>
                </configuration>
            </plugin>
```





然后将jar包上传到服务器， `java -jar easypan.jar`



此外如果想要覆盖yml中的配置文件，可以使用 -D参数指定jvm级别的环境变量。



支持通过环境变量动态修改的参数有

```
MYSQL_HOST     127.0.0.1
MYSQL_PORT		3306
MYSQL_DATABASE	cloudDisks
REDIS_HOST		127.0.0.1
REDIS_PORT	6379
REDIS_DATABASE	0
```







