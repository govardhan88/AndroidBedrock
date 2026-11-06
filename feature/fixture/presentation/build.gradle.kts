plugins {
    id("govi.android.feature")
}

android {
    namespace = "govi.feature.fixture.presentation"
}

dependencies {
    implementation(project(":feature:fixture:domain"))
    implementation(project(":core:navigation"))
}
