plugins {
    id("govi.android.feature")
}

android {
    namespace = "govi.feature.users.presentation"
}

dependencies {
    implementation(project(":feature:users:domain"))
    implementation(project(":core:navigation"))
}
