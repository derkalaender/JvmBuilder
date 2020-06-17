package de.derkalaender.jvmbuilder

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

internal val KEY_ENABLED = CompilerConfigurationKey<Boolean>("enabled")

internal val KEY_JVM_BUILDER_ANNOTATION = CompilerConfigurationKey<String>("jvmBuilderAnnotation")

@AutoService(CommandLineProcessor::class)
class JvmBuilderCommandLineProcessor : CommandLineProcessor {
  override val pluginId = "jvm-builder-compiler-plugin"

  override val pluginOptions =
      listOf(
          CliOption("enabled", "<true | false>", "", true),
          CliOption("jvmBuilderAnnotation", "String", "", true))

  override fun processOption(
      option: AbstractCliOption, value: String, configuration: CompilerConfiguration
  ) =
      when (option.optionName) {
        "enabled" -> configuration.put(KEY_ENABLED, value.toBoolean())
        "jvmBuilderAnnotation" -> configuration.put(KEY_JVM_BUILDER_ANNOTATION, value)
        else -> error("Unknown plugin option: ${option.optionName}")
      }
}
