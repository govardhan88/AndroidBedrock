plugins {
    id("govi.android.feature")
}

android {
    namespace = "govi.feature.products.presentation"
}

dependencies {
    implementation(project(":feature:products:domain"))
    implementation(project(":core:navigation"))
}
