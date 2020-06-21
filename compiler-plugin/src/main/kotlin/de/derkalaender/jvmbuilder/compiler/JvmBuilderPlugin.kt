package de.derkalaender.jvmbuilder.compiler

import com.google.auto.service.AutoService
import de.derkalaender.jvmbuilder.compiler.codegen.ClassBuilderInterceptorTest
import de.derkalaender.jvmbuilder.compiler.codegen.JvmBuilderCodegenExtension
import de.derkalaender.jvmbuilder.compiler.codegen.JvmBuilderSyntheticResolveExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.extensions.ClassBuilderInterceptorExtension
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.impl.ExtensionPointImpl
import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension

@AutoService(ComponentRegistrar::class)
class JvmBuilderPlugin : ComponentRegistrar {
  override fun registerProjectComponents(
    project: MockProject, configuration: CompilerConfiguration
  ) {
    if (configuration[CompilerKeys.ENABLED] == false) return

    val messageCollector =
      configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
    val jvmBuilderAnnotation = checkNotNull(configuration[CompilerKeys.JVM_BUILDER_ANNOTATION])
    val fqJvmBuilderAnnotation = FqName(jvmBuilderAnnotation)

    ExpressionCodegenExtension.registerExtensionAsFirst(
      project, JvmBuilderCodegenExtension(messageCollector, fqJvmBuilderAnnotation)
    )

    SyntheticResolveExtension.registerExtensionAsFirst(
      project, JvmBuilderSyntheticResolveExtension(messageCollector, fqJvmBuilderAnnotation)
    )
  }
}

fun <T> ProjectExtensionDescriptor<T>.registerExtensionAsFirst(project: Project, extension: T) {
  project.extensionArea
    .getExtensionPoint(extensionPointName)
    .let { it as ExtensionPointImpl }
    .registerExtension(extension, project)
}
