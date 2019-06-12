# NOTE: Multi-stage Build

FROM nwtgck/pre-installed-sbt:1.2.8 as build

# Copy all things in this repo except files in .dockerignore
COPY . /app
# Move to /app
WORKDIR /app
# Generate jar
RUN sbt assembly

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/target/jar/gh-card.jar"]



## Open JDK 8 - Alpine
#FROM openjdk:8-alpine
#LABEL maintainer="Ryo Ota <nwtgck@gmail.com>"
#
## Make directories
#RUN mkdir -p /app/backend
#
## Copy artifact
#COPY --from=build /app/target/jar/gh-card.jar /app/backend/gh-card.jar
#
## Run entry (Run the server)
#ENTRYPOINT ["java", "-jar", "/app/backend/gh-card.jar"]
