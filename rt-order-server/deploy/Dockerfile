FROM java

MAINTAINER "Eugene Prystupa"

ADD ./target /app

CMD ["java", "-cp", "/app/dependency/*:/app/rt-order-server-1.0-SNAPSHOT.jar", "com.prystupa.server.StoreApp"]