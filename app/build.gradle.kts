plugins {
    alias(libs.plugins.android.application)
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "hcmute.edu.vn.pharmagnosis"
    compileSdk = 36

    defaultConfig {
        applicationId = "hcmute.edu.vn.pharmagnosis"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Các thư viện UI và Core cơ bản của Android
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Thư viện Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firebase BoM (Bill of Materials) - Quản lý phiên bản chung cho mọi dịch vụ Firebase
    implementation(platform("com.google.firebase:firebase-bom:34.11.0"))

    // Các dịch vụ Firebase (Khi đã có BoM thì không cần ghi version ở đây nữa)
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database")
    implementation(libs.firebase.storage)

    // Kiến trúc MVVM (ViewModel & LiveData)
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.2")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.2")

    // Thư viện Glide để tải ảnh từ mạng (Network Image Loading)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Thư viện GSON để đóng gói/mở gói dữ liệu JSON
    implementation("com.google.code.gson:gson:2.10.1")
    // Thư viện OpenStreetMap (Bản đồ miễn phí)
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    // Vẫn giữ lại thư viện này để lấy vị trí GPS hiện tại của User
    implementation("com.google.android.gms:play-services-location:21.1.0")
    // Thư viện Retrofit để gọi API và Gson để đọc dữ liệu JSON
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}