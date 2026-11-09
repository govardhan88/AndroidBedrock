package com.govi.androidbedrock.buildlogic.scaffolding

import org.gradle.api.Plugin
import org.gradle.api.Project

class ScaffoldingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.tasks.register("createFeature", CreateFeatureTask::class.java)
    }
}
