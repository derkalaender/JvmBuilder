package de.derkalaender.jvmbuilder.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import de.derkalaender.jvmbuilder.compiler.codegen.JvmBuilderCommandLineProcessor
import de.derkalaender.jvmbuilder.compiler.codegen.JvmBuilderPlugin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.config.JvmTarget

class JvmBuilderTest : StringSpec() {
  private val jvmBuilderAnnotation =
      kotlin(
          "JvmBuilderAnnotation.kt",
          """
          package de.derkalaender.jvmbuilder.test
          
          import kotlin.annotation.AnnotationRetention
          
          @Retention(AnnotationRetention.BINARY)
          annotation class JvmBuilder
          """.trimIndent())

  init {
    "dataIsRequired" {
      val result =
          compile(
              kotlin(
                  "JvmBuilder.kt",
                  """
                  package de.derkalaender.jvmbuilder.test
          
                  @JvmBuilder
                  class Test(val test: String)
                  """.trimIndent()))
      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
    }
  }

  private fun prepareCompilation(vararg sourceFiles: SourceFile): KotlinCompilation {
    return KotlinCompilation().apply {
      commandLineProcessors = listOf(JvmBuilderCommandLineProcessor())
      compilerPlugins = listOf(JvmBuilderPlugin())
      pluginOptions =
          listOf(
              createPluginOption("enabled", true),
              createPluginOption(
                  "jvmBuilderAnnotation", "de.derkalaender.jvmbuilder.test.JvmBuilder"))
      inheritClassPath = true
      sources = sourceFiles.asList() + jvmBuilderAnnotation
      verbose = false
      jvmTarget = JvmTarget.fromString(System.getenv()["ci_java_version"] ?: "1.8")!!.description
    }
  }

  private fun createPluginOption(name: String, value: Any): PluginOption {
    return PluginOption("jvm-builder-compiler-plugin", name, value.toString())
  }

  private fun compile(vararg sourceFiles: SourceFile): KotlinCompilation.Result {
    return prepareCompilation(*sourceFiles).compile()
  }
}
