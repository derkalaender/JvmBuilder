package de.derkalaender.jvmbuilder.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

const val DEFAULT_ANNOTATION = "de.derkalaender.jvmbuilder.annotations.JvmBuilder"

class JvmBuilderGradlePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    project.extensions.create("jvmBuilder", JvmBuilderPluginExtension::class.java)
  }
}

open class JvmBuilderPluginExtension {
  var jvmBuilderAnnotation = DEFAULT_ANNOTATION
  var enabled = true
}
