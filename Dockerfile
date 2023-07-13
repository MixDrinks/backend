FROM gradle:8.2.1-jdk11 AS build

COPY . /appbuild
WORKDIR /appbuild

RUN gradle clean build
RUN gradle buildFatJar

FROM openjdk:11.0.16

ENV APPLICATION_USER 1033
RUN useradd -ms /bin/bash $APPLICATION_USER

ARG MIX_DRINKS_APP_VERSION=arg_version_to_be_replaced
ENV MIX_DRINKS_APP_VERSION ${MIX_DRINKS_APP_VERSION}

COPY --from=build /appbuild/build/libs/MixDrinks.jar /app/MixDrinks.jar
COPY --from=build /appbuild/src/main/resources/ /app/resources/
COPY --from=build /appbuild/static/ /app/static/

RUN chown -R $APPLICATION_USER /app
RUN chmod -R 755 /app

USER $APPLICATION_USER

WORKDIR /app

ENTRYPOINT ["java","-jar","/app/MixDrinks.jar"]