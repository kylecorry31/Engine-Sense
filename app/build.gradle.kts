plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
}

android {
    compileSdk = 31

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        applicationId = "com.kylecorry.enginesense"
        minSdk = 23
        targetSdk = 31
        versionCode = 1
        versionName = "0.1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packagingOptions {
        resources.merges += "META-INF/LICENSE.md"
        resources.merges += "META-INF/LICENSE-notice.md"
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }

    lint {
        abortOnError = false
    }
    namespace = "com.kylecorry.enginesense"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.6.21")
    implementation("androidx.appcompat:appcompat:1.4.2")
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.gridlayout:gridlayout:1.0.0")
    implementation("androidx.preference:preference-ktx:1.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.3.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.3.5")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.lifecycle:lifecycle-service:2.4.1")
    implementation("com.google.android.material:material:1.6.1")
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.5")
    // Andromeda
    val andromedaVersion = "4.0.0-beta03"
    implementation("com.github.kylecorry31.andromeda:core:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:fragments:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:bluetooth:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:preferences:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:permissions:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:notify:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:alerts:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:pickers:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:list:$andromedaVersion")
    implementation("com.github.kylecorry31.andromeda:clipboard:$andromedaVersion")

    implementation("com.github.pires:obd-java-api:1.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    testImplementation("org.junit.platform:junit-platform-runner:1.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.8.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}
