cp server-0.0.1-SNAPSHOT.jar server.jar

docker build  -t server:v1 .

docker stop server

docker rm -f server

docker run -itd  --name server \
    --hostname server \
    --privileged=true \
    -v /data/server_myt:/opt \
    -p 9002:8000 \
    server:v1
