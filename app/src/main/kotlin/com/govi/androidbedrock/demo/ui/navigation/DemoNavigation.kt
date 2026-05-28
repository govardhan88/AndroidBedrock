package com.govi.androidbedrock.demo.ui.navigation

sealed class DemoNavigation(val title: String) {
    object Main : DemoNavigation("Core Modules")
    object ResourceProvider : DemoNavigation("Resource Provider")
    object Networking : DemoNavigation("Networking")
    object Storage : DemoNavigation("Secure Storage")
    object Dispatchers : DemoNavigation("Dispatchers")
}
