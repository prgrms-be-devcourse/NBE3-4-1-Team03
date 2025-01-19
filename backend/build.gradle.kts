plugins {
    java
    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.app"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    //Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    //Jackson Datatype: JSR310
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    //Querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    //JJWT :: API
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")

    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    annotationProcessor("org.projectlombok:lombok")
    //Jakarta Annotations API
    annotationProcessor("jakarta.annotation:jakarta.annotation-api")
    //Jakarta Persistence API
    annotationProcessor("jakarta.persistence:jakarta.persistence-api")
    //Querydsl
    annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
//    //== GreenMail Junit5 ==
//    testImplementation("com.icegreen:greenmail-junit5:1.6.15")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//Querydsl - Start
val generatedDir = file("src/main/generated")

sourceSets {
    main {
        java {
            srcDir(generatedDir)
        }
    }
}

tasks {
    compileJava {
        options.annotationProcessorGeneratedSourcesDirectory = generatedDir
    }

    clean {
        doFirst {
            delete(generatedDir)
        }
    }
}
//Querydsl - End
