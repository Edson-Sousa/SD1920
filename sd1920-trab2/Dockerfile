# base image - an image with openjdk  8
FROM nunopreguica/sd1920tpbase

# working directory inside docker image
WORKDIR /home/sd

# copy the jar created by assembly to the docker image
COPY target/*jar-with-dependencies.jar sd1920.jar

## copy server key (keystore.ks)
COPY server.ks server.ks

## copy client truststore key (truststore.ks)
COPY truststore.ks truststore.ks

# copy the file of properties to the docker image
COPY messages.props messages.props

# run Discovery when starting the docker image
CMD ["java", "-Djavax.net.ssl.keyStore=/home/git/SD1920/sd1920-trab2/server.ks"
			 "-Djavax.net.ssl.keyStorePassword=password"
			 "-Djavax.net.ssl.trustStore=/home/git/SD1920/sd1920-trab2/truststore.ks"
			 "-Djavax.net.ssl.trustStorePassword=changeit"
			 "-cp", "/home/sd/sd1920.jar", "sd1920.trab1.EmailServerRest"]
