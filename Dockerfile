FROM gradle:8.0.2-jdk11 AS build

COPY . /appbuild
WORKDIR /appbuild

RUN gradle clean build
RUN gradle buildFatJar

FROM openjdk:11

ENV APPLICATION_USER 1033
RUN useradd -ms /bin/bash $APPLICATION_USER

COPY --from=build /appbuild/build/libs/MixDrinks.jar /app/MixDrinks.jar
COPY --from=build /appbuild/src/main/resources/ /app/resources/

RUN chown -R $APPLICATION_USER /app
RUN chmod -R 755 /app

USER $APPLICATION_USER

WORKDIR /app

ENTRYPOINT ["java","-jar","/app/MixDrinks.jar"]