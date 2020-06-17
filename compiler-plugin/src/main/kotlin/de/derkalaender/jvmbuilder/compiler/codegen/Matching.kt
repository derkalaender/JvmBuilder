package de.derkalaender.jvmbuilder.compiler.codegen

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.resolve.source.getPsi

fun ClassDescriptor.isJvmBuilderApplicable(
    jvmBuilderAnnotation: FqName, messageCollector: MessageCollector
): Boolean {
  if (annotations.hasAnnotation(jvmBuilderAnnotation)) {
    if (isData) {
      return true
    } else {
      messageCollector.report(
          CompilerMessageSeverity.ERROR,
          "@JvmBuilder is only supported on data classes!",
          MessageUtil.psiElementToMessageLocation(source.getPsi()))
    }
  }
  return false
}
