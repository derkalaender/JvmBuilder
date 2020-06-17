package de.derkalaender.jvmbuilder.compiler.codegen

import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.name.FqName

class JvmBuilderCodegenExtension(
    private val messageCollector: MessageCollector, private val fqJvmBuilderAnnotation: FqName
) : ExpressionCodegenExtension {
  override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
    val targetClass = codegen.descriptor

    if (!targetClass.isJvmBuilderApplicable(fqJvmBuilderAnnotation, messageCollector)) return
  }
}
