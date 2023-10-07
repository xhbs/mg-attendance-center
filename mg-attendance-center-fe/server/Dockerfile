# FROM 192.168.110.202/unisinsight/node-alpine:1.0
# FROM 192.168.110.202/unisinsight/base-node:12.18.4-alpine3.12
FROM 10.209.8.12:8090/cg/weiyun-base-images-nodejs-12.18.4:T202011051009
ADD . /opt/app/
WORKDIR /opt/app/
ENTRYPOINT ["/bin/sh","-c","node /opt/app/bin/www && tail -f /dev/null"]
