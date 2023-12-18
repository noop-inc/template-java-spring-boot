# template-java-spring-boot
Build and deploy a spring boot app. This template is configured with a Postgres database.

## Overview

## Setup

### Noop dependencies

Make sure you have [Workshop](https://noop.dev/docs/installation/) installed locally or a Github account connected to Noop Cloud. The remaining setup is identical in both.

### Local Setup

Ensure you have Java (preferably [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or later) and [maven]https://maven.apache.org/download.cgi installed. When running maven commands from the terminal and not an IDE, make sure the JAVA_HOME environment variable resolves to the JDK 17 directory.

In the project root directory, to pull in plugins and dependencies, run:

```bash
mvn package -DskipTests
```

When maven has finished running, it will output an executable jar to the target folder within the project base directory. 

To start the spring boot application server, run the following from the project root:

```bash
java -jar ./target/springboot-0.0.1-SNAPSHOT-exec.jar
```

Noop Workshop and Cloud will auto-provision a Postgres database and dynamically inject relevant environment variables into the application container. If you have your own Postgres database running through psql or a docker container, consider editing the default values in the application config in accordance with your setup:

```yaml
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DATABASE:}
    username: ${POSTGRES_USER:postgres}
    password: ${POSTGRES_PASSWORD:example}
    driver-class-name: org.postgresql.Driver
```

### Template configuration

The `.noop/blueprint.yml` file contains everything we need to build and deploy the spring-boot Noop template.

The POSTGRES variables in this yaml file inject the environment configuration values we need for spring-jpa autoconfiguration on application startup.

```yaml
---
components:
  - name: SpringApp
    type: service
    image:  maven:3-eclipse-temurin-17-alpine
    port: 8080
    build:
      steps:
        - directory: /app
        - copy: [pom.xml]
          destination: ./
        - copy: src
          destination: ./src
        - run: mvn package -DskipTests
        - image: eclipse-temurin:17-jdk-alpine
          stage: spring
        - copy: /app/target/*-exec.jar
          destination: /application.jar
          from: main
    runtime:
      command: java -jar /application.jar
      resources:
        - PgDatabase
      variables:
        POSTGRES_HOST:
          $resources: PgDatabase.host
        POSTGRES_USER:
          $resources: PgDatabase.username
        POSTGRES_PASSWORD:
          $resources: PgDatabase.password
        POSTGRES_PORT:
          $resources: PgDatabase.port
        POSTGRES_DATABASE:
          $resources: PgDatabase.database
        PORT: 8080
routes:
  - target:
      component: SpringApp

resources:
  - name: PgDatabase
    type: postgresql
```

The spring application configuration will read noop database bindings for environment variables defined for the database host, database name, user, and password. Default values are provided in case the application container starts up prior to database initialization. 

To automate the Application setup and Environment creation, including database provisioning and application deployment, we create a Runbook in the `.noop/runbooks` directory.

Here is the runbook content:

```yaml
name: Quickstart Setup

description: Demo full stack setup of a Spring Boot application with internet endpoint

workflow:
  inputs:
    - name: EnvironmentName
      description: What should we name your new environment?
      type: string
      required: true
      default: Quickstart Demo

    - name: Cluster
      type: Cluster
      description: Which cluster should the environment launch on?
      required: true

  timeout: 300

  steps:
    - name: Environment
      action: EnvironmentCreate
      params:
        name:
          $inputs: EnvironmentName
        production: false
        appId:
          $runbook: Application.id
        clusterId:
          $inputs: Cluster.id

    - name: Build
      action: BuildExecute
      params:
        sourceCodeId:
          $runbook: SourceCode.id
        appId:
          $runbook: Application.id

    - name: Resources
      action: ResourceLaunch
      params:
        envId:
          $steps: Environment.id
        sourceCodeId:
          $runbook: SourceCode.id

    - name: Deploy
      action: DeploymentExecute
      params:
        envId:
          $steps: Environment.id
        buildId:
          $steps: Build.id

    - name: Endpoint
      action: InternetEndpointRandom
      params:
        orgId:
          $runbook: Organization.id
        routes:
          - name: 'Demo Environment'
            target:
              environments:
                - $steps: Environment.id
```

```yaml
spring:
# ...
  datasource:
    url: jdbc:postgresql://${POSTGRES_HOST:localhost}:${POSTGRES_PORT:5432}/${POSTGRES_DATABASE:}
    username: ${POSTGRES_USER:postgres}
    password: ${POSTGRES_PASSWORD:example}
    driver-class-name: org.postgresql.Driver
    hikari:
      minimum-idle: 0
      initialization-fail-timeout: -1
  jpa:
    hibernate:
      ddl-auto: none
    database-platform: org.hibernate.dialect.PostgreSQLDialect
  sql:
    init:
      continue-on-error: true
```

### Extending the Blueprint

The spring-boot base setup for this Noop template, and all maven dependencies, were auto-generated using [Spring Initializr](https://start.spring.io/). The application source is roughly based on this [spring-jpa-thymeleaf tutorial](https://www.javaguides.net/2018/10/user-registration-module-using-springboot-springmvc-springsecurity-hibernate5-thymeleaf-mysql.html).

To add additional dependencies or create your own spring microservice on Noop, the easiest way is to generate a spring-boot project through initializr and ensure you select "Project: Maven", "Language: Java", and "Packaging: Jar." The Noop app manifest depends on the use of maven as a package manager and deploys using an executable jar.

The generated initializr project will be a zip file with boilerplate source and project dependencies. Follow spring best-practices and remove maven wrapper-scripts by deleting the following mvn directory and files:
- .mvn/
- mvnw
- mvnw.bat

The spring-boot Noop template uses a multi-stage build process with the [maven:3.8.5-eclipse-temurin-17-alpine](https://hub.docker.com/layers/library/maven/3.8.5-eclipse-temurin-17-alpine/images/sha256-df1555c151a92a2464944bbbf08057d06414b92393ba9b52566782479eefc07c) image, so the maven wrapper scripts and jar are unnecessary.

In order to leverage the build and deployment process used in this spring-boot template's app manifest, add the spring-boot-maven-plugin with an executable jar classifer to your pom.xml:

```xml
<plugins>
<!-- Include -->
    <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
        <executions>
            <execution>
                <id>repackage</id>
                <configuration>
                    <classifier>exec</classifier>
                </configuration>
            </execution>
        </executions>
    </plugin>
</plugins>
```

You can then proceed to build out your application, and even add multiple spring modules to your project, while maintaining the way in which we build and deploy the executable jar in this blueprint. 

