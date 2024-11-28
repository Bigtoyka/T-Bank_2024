plugins {
    id("java")
}

group = "org.tbank"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.4")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.3.4")
    testImplementation("org.springframework.boot:spring-boot-test-autoconfigure:3.3.4")
    testImplementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.projectlombok:lombok:1.18.34")
    implementation ("ch.qos.logback:logback-classic:1.5.12")
// https://mvnrepository.com/artifact/org.springframework.boot/spring-boot-starter-actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator:3.4.0")
    // Lombok
    compileOnly ("org.projectlombok:lombok:1.18.34")
    annotationProcessor ("org.projectlombok:lombok:1.18.34")
    testCompileOnly ("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor ("org.projectlombok:lombok:1.18.34")
    // https://mvnrepository.com/artifact/io.micrometer/micrometer-registry-prometheus
    implementation("io.micrometer:micrometer-registry-prometheus:1.14.1")

}

tasks.test {
    useJUnitPlatform()
}