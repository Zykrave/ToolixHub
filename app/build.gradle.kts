import java.util.Properties
import java.io.FileInputStream

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
}

android {
  namespace = "com.zykrave.toolixhub"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.zykrave.toolixhub"
    minSdk = 24
    targetSdk = 36
    versionCode = 2
    versionName = "1.1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePropsFile = rootProject.file("Keys/keystore.properties")
      val keystoreProps = Properties()
      if (keystorePropsFile.exists()) {
        FileInputStream(keystorePropsFile).use { stream ->
          keystoreProps.load(stream)
        }
      }

      val propStoreFile = keystoreProps.getProperty("storeFile")
      val propStorePassword = keystoreProps.getProperty("storePassword")
      val propKeyAlias = keystoreProps.getProperty("keyAlias")
      val propKeyPassword = keystoreProps.getProperty("keyPassword")

      val resolvedStoreFilePath = if (!propStoreFile.isNullOrBlank()) {
        propStoreFile
      } else {
        System.getenv("KEYSTORE_PATH") ?: "Keys/toolixhub-release.jks"
      }

      storeFile = rootProject.file(resolvedStoreFilePath)
      storePassword = propStorePassword
        ?: System.getenv("STORE_PASSWORD")
        ?: "YOUR_STORE_PASSWORD"
      keyAlias = propKeyAlias
        ?: System.getenv("KEY_ALIAS")
        ?: "toolix-release"
      val rawKeyPassword = propKeyPassword ?: System.getenv("KEY_PASSWORD")
      keyPassword = if (rawKeyPassword.isNullOrBlank() || rawKeyPassword == "YOUR_KEY_PASSWORD") {
        storePassword
      } else {
        rawKeyPassword
      }
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// 100% offline app: no networking, no Firebase, no Gemini/server-side AI,
// no local database. Everything below is either Compose UI, a local
// on-device library (DataStore for saved prefs, ZXing for QR/barcode
// encode-decode), or test tooling.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.zxing.core)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
}
