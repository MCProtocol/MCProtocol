/*
 * Copyright (c) 2018 - 2019 Cubxity, superblaubeere27 and KodingKing1
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    id("maven")
    id("io.spring.dependency-management") version "1.0.7.RELEASE"
}

group = "dev.cubxity.mc.protocol"
version = "1.0"

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://jitpack.io") }
}

dependencyManagement {
    imports {
        mavenBom("io.projectreactor:reactor-bom:Californium-RELEASE")
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.3.0-RC")
    compile("io.projectreactor.kotlin:reactor-kotlin-extensions:1.0.0.M1")
    compile("io.projectreactor.netty:reactor-netty")
    //compile("com.google.code.gson:gson")
    compile("ch.qos.logback:logback-classic:1.3.0-alpha4")
    compile("org.objenesis:objenesis:3.0.1")
    compile("com.github.Steveice10:MCAuthLib:1.0")
    compile("com.github.Steveice10:OpenNBT:1.2")
    compile("org.apache.commons:commons-lang3:3.9")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}