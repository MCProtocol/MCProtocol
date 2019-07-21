import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

group = "dev.cubxity.mc.protocol"
version = "1.0"

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.projectreactor:reactor-bom:Californium-RELEASE")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("io.reactivex.rxjava2:rxkotlin:2.4.0-RC3")
    compile("io.projectreactor.netty:reactor-netty")
    compile("ch.qos.logback:logback-classic:1.3.0-alpha4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}