plugins { id("com.android.application") }
android {
    namespace = "com.muly.couple"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.muly.couple"
        minSdk = 26
        targetSdk = 34
        versionCode = 200
        versionName = "2.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
