package de.derkalaender.jvmbuilder.compiler

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CliOptionProcessingException
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

object OptionNames {
  const val ENABLED = "enabled"
  const val JVM_BUILDER_ANNOTATION = "jvmBuilderAnnotation"
}

object CompilerKeys {
  val ENABLED = CompilerConfigurationKey<Boolean>(OptionNames.ENABLED)
  val JVM_BUILDER_ANNOTATION = CompilerConfigurationKey<String>(OptionNames.JVM_BUILDER_ANNOTATION)
}

@AutoService(CommandLineProcessor::class)
class JvmBuilderCommandLineProcessor : CommandLineProcessor {
  override val pluginId = "jvmbuilder-compiler-plugin"

  override val pluginOptions =
      listOf(
          CliOption(
              optionName = "enabled",
              valueDescription = "<true | false>",
              description = "Whether or not this compiler plugin is enabled.",
              required = true),
          CliOption(
              optionName = "jvmBuilderAnnotation",
              valueDescription = "String",
              description = "A fully qualified name of the @JvmBuilder annotation to look for.",
              required = true))

  override fun processOption(
      option: AbstractCliOption, value: String, configuration: CompilerConfiguration
  ) =
      when (option.optionName) {
        OptionNames.ENABLED -> configuration.put(CompilerKeys.ENABLED, value.toBoolean())
        OptionNames.JVM_BUILDER_ANNOTATION ->
            configuration.put(CompilerKeys.JVM_BUILDER_ANNOTATION, value)
        else -> throw CliOptionProcessingException("Unknown plugin option: ${option.optionName}")
      }
}
