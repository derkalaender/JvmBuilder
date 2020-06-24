package de.derkalaender.jvmbuilder.gradle

import com.google.auto.service.AutoService
import org.gradle.api.Project
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonOptions
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinGradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

@AutoService(KotlinGradleSubplugin::class)
class JvmBuilderGradleSubplugin : KotlinGradleSubplugin<AbstractCompile> {
  override fun isApplicable(project: Project, task: AbstractCompile) =
      project.plugins.hasPlugin(JvmBuilderGradlePlugin::class.java)

  override fun getCompilerPluginId() = "jvmbuilder-compiler-plugin"

  override fun getPluginArtifact() =
      SubpluginArtifact("de.derkalaender.jvmbuilder", "jvmbuilder-compiler-plugin", "0.1")

  override fun apply(
      project: Project,
      kotlinCompile: AbstractCompile,
      javaCompile: AbstractCompile?,
      variantData: Any?,
      androidProjectHandler: Any?,
      kotlinCompilation: KotlinCompilation<KotlinCommonOptions>?
  ): List<SubpluginOption> {
    val extension =
        project.extensions.findByType(JvmBuilderPluginExtension::class.java)
            ?: JvmBuilderPluginExtension()
    val annotation = extension.jvmBuilderAnnotation

    if (annotation == DEFAULT_ANNOTATION) {
      project.dependencies
          .add("implementation", "de.derkalaender.jvmbuilder:jvmbuilder-annotations:0.1")
    }

    val enabled = extension.enabled

    return listOf(
        SubpluginOption("enabled", enabled.toString()),
        SubpluginOption("jvmBuilderAnnotation", annotation))
  }
}
