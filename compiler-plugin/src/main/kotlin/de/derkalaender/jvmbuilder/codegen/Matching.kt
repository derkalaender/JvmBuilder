package de.derkalaender.jvmbuilder.codegen

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.name.FqName

fun ClassDescriptor.isJvmBuilderApplicable(jvmBuilderAnnotation: FqName): Boolean {
  return annotations.hasAnnotation(jvmBuilderAnnotation) && isData
}
