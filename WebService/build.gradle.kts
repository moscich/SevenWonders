group = "com.moscichowski"
version = "0.0.1"

plugins {
	`maven-publish`
    kotlin("jvm") version "1.2.31"
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib", "1.2.31"))
    compile(project(":Wonders"))
    testImplementation("junit:junit:4.12")
    testImplementation("org.jetbrains.kotlin:kotlin-test:1.1.51")
}

publishing {
    publications {
        create("default", MavenPublication::class.java) { 
            from(components["java"])
        }
    }
    repositories {
        maven {
            url = uri("$buildDir/repository") 
        }
    }
}