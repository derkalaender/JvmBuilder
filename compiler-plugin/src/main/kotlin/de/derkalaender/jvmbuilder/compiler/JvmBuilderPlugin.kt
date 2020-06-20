package de.derkalaender.jvmbuilder.compiler

import com.google.auto.service.AutoService
import de.derkalaender.jvmbuilder.compiler.codegen.JvmBuilderCodegenExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.name.FqName

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

    ExpressionCodegenExtension.registerExtension(
        project, JvmBuilderCodegenExtension(messageCollector, fqJvmBuilderAnnotation))
  }
}
