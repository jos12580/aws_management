FROM openjdk:21

WORKDIR /opt
COPY . /opt
COPY run.sh  /opt
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone
RUN echo "java  -Xmx1024M -XX:MaxMetaspaceSize=512m   -jar /opt/server.jar --spring.config.location=application.yml"  >> /root/start.sh && \
    echo "tail -f /root/start.sh" >> /root/start.sh && \
    chmod 700 /root/start.sh
CMD /root/start.sh