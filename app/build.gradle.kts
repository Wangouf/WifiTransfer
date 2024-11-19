plugins {
    id("com.android.application")
}

android {
    namespace = "com.zhangyue.wifitransfer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zhangyue.wifitransfer"
        minSdk = 16
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("androidx.appcompat:appcompat:1.5.0")
    implementation("com.google.zxing:core:3.2.1")


    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //implementation("com.google.android.material:material:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}