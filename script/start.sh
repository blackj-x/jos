#!/usr/bin/env bash

#需根据情况 替换启动用户和相应各种路径
sudo runuser tomcat -s /bin/bash -c "nohup /usr/java/default/bin/java -jar -Djava.io.tmpdir=/opt/jos/temp/ /opt/jos/jd-proxy.jar --spring.config.location=/opt/jos/application.properties &> nohup.out &"
sleep 5s
echo "start ok"