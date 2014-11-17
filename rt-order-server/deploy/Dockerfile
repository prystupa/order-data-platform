FROM java

MAINTAINER "Eugene Prystupa"

ADD . /app

EXPOSE  5701

CMD java -Daws.access-key=$AWS_ACCESS_KEY -Daws.secret-key=$AWS_SECRET_KEY -cp '/app/*' com.prystupa.server.StoreApp
