# template-java-spring-boot
Spring-boot app built on noop

## Project Setup

The spring-boot base setup for this Noop template, and all maven dependencies, were auto-generated using [Spring Initializr](https://start.spring.io/). To add additional dependencies, generate a spring-boot project through initializr and ensure you select "Project: Maven", "Language: Java", and "Packaging: Jar," as the Noop app manifest depends on the use of maven as a package manager and deploys using an executable jar.

After Initializr generates a zip file with the project source, you can immediately delete the following mvn directory and files:
- .mvn/
- mvnw
- mvnw.bat

The spring-boot Noop template uses a multi-stage build process with the [maven:3.8.5-eclipse-temurin-17-alpine](https://hub.docker.com/layers/library/maven/3.8.5-eclipse-temurin-17-alpine/images/sha256-df1555c151a92a2464944bbbf08057d06414b92393ba9b52566782479eefc07c); [see here](https://stackoverflow.com/questions/47240546/should-the-mvnw-files-be-added-to-the-repository/47240726#47240726) for an explanation as to why the files are being removed.


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

