import com.danilp.buildsrc.Deps

plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "1.7.20-1.0.6"
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.danilp.professionalaquarist.android"
    compileSdk = 33
    defaultConfig {
        applicationId = "com.danilp.professionalaquarist.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }
    applicationVariants.all {
        kotlin.sourceSets {
            getByName(name) {
                kotlin.srcDir("build/generated/ksp/$name/kotlin")
            }
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.0")

    with(Deps.Compose) {
        implementation(ui)
        implementation(uiPreview)
        implementation(material3)
        implementation(viewModel)
        implementation(animation)
        implementation(foundation)
        implementation(foundationLayout)
        implementation(icons)
        implementation(swipeRefresh)
        implementation(navigation)
        androidTestImplementation(unitTest)
        debugImplementation(debugTest)
        debugImplementation(debugTooling)
    }

    with(Deps.Destinations) {
        implementation(core)
        ksp(ksp_)
    }

    with(Deps.Coroutines) {
        implementation(core)
        implementation(android)
    }

    with(Deps.DaggerHilt) {
        implementation(android)
        kapt(compiler)
        implementation(composeNavigation)
        kapt(composeCompiler)
    }

    implementation("com.github.skydoves:landscapist-glide:1.6.1")
}