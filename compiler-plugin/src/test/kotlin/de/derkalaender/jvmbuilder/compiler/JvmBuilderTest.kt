package de.derkalaender.jvmbuilder.compiler

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.PluginOption
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.SourceFile.Companion.kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.jetbrains.kotlin.config.JvmTarget

const val defaultAnnotation = "de.derkalaender.jvmbuilder.annotations.JvmBuilder"

class JvmBuilderTest : StringSpec() {
  init {
    "requires annotation" {
      var result =
          compile(
              kotlin(
                  "RequiresAnnotation.kt",
                  """
                  package de.derkalaender.jvmbuilder.test
                  
                  data class HasNotAnnotation(val someProp: String)
                  """.trimIndent()))
      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
      result.messages shouldNotContain "@JvmBuilder > Processing annotated class HasNotAnnotation"

      result =
          compile(
              kotlin(
                  "RequiresAnnotation.kt",
                  """
                  package de.derkalaender.jvmbuilder.test
                  
                  import de.derkalaender.jvmbuilder.annotations.JvmBuilder
                  
                  @JvmBuilder
                  data class HasAnnotation(val someProp: String)
                  """.trimIndent()))
      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
      result.messages shouldContain "@JvmBuilder > Processing annotated class HasAnnotation"
    }

    "any annotation works" {
      val result =
          compile(
              createCustomAnnotation("SomeAnnotation"),
              kotlin(
                  "SomeAnnotatedClass.kt",
                  """
                  package de.derkalaender.jvmbuilder.test
          
                  @SomeAnnotation
                  data class SomeAnnotatedClass(val someProp: String)
                  """.trimIndent()),
              annotation = "de.derkalaender.jvmbuilder.test.SomeAnnotation")
      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
      result.messages shouldContain "@JvmBuilder > Processing annotated class SomeAnnotatedClass"
    }

    "data is required" {
      val result =
          compile(
              kotlin(
                  "NonDataClass.kt",
                  """
                  package de.derkalaender.jvmbuilder.test
                  
                  import de.derkalaender.jvmbuilder.annotations.JvmBuilder
          
                  @JvmBuilder
                  class NonDataClass
                  """.trimIndent()))
      result.exitCode shouldBe KotlinCompilation.ExitCode.COMPILATION_ERROR
      result.messages shouldContain "@JvmBuilder > Only data classes are supported!"
    }

    "simple test" {
      val result =
          compile(
              kotlin(
                  "SimpleTest.kt",
                  """
                  package de.derkalaender.jvmbuilder.test
                  
                  import de.derkalaender.jvmbuilder.annotations.JvmBuilder
          
                  @JvmBuilder
                  data class SimpleTest(val defaultHaving: Boolean = true, val notDefaultHaving: Boolean)
                  """.trimIndent()))
      result.exitCode shouldBe KotlinCompilation.ExitCode.OK
    }
  }

  private fun createCustomAnnotation(name: String = "JvmBuilder") =
      kotlin(
          "JvmBuilder.kt",
          """
          package de.derkalaender.jvmbuilder.test
          
          @Retention(AnnotationRetention.BINARY)
          @Target(AnnotationTarget.CLASS)
          annotation class $name
          """.trimIndent())

  private fun prepareCompilation(
      vararg sourceFiles: SourceFile, annotation: String = defaultAnnotation
  ): KotlinCompilation {
    return KotlinCompilation().apply {
      commandLineProcessors = listOf(JvmBuilderCommandLineProcessor())
      compilerPlugins = listOf(JvmBuilderPlugin())
      pluginOptions =
          listOf(
              createPluginOption("enabled", true),
              createPluginOption("jvmBuilderAnnotation", annotation))
      inheritClassPath = true
      sources = sourceFiles.asList()
      verbose = false
      jvmTarget = JvmTarget.fromString(System.getenv()["ci_java_version"] ?: "1.8")!!.description
    }
  }

  private fun createPluginOption(name: String, value: Any): PluginOption {
    return PluginOption("jvmbuilder-compiler-plugin", name, value.toString())
  }

  private fun compile(
      vararg sourceFiles: SourceFile, annotation: String = defaultAnnotation
  ): KotlinCompilation.Result {
    return prepareCompilation(*sourceFiles, annotation = annotation).compile()
  }
}
