plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "edu.uw.ischool.shiina12.tasknest"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.uw.ischool.shiina12.tasknest"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    packaging {
        resources.excludes.add("META-INF/*")
    }
}

configurations {
    implementation {
        exclude("com.google.guava", "guava-jdk5")
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference-ktx:1.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.code.gson:gson:2.9.0")

    // API Dependencies
    implementation("com.google.oauth-client:google-oauth-client-jetty:1.34.1")
    implementation("com.google.apis:google-api-services-calendar:v3-rev305-1.23.0")
//    implementation("com.google.apis:google-api-services-gmail:v1-rev83-1.31.5")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.guava:guava:31.1-android")
    implementation("com.google.api-client:google-api-client-android:1.23.0") {
        exclude("org.apache.httpcomponents")
    }
    implementation("pub.devrel:easypermissions:3.0.0")

}