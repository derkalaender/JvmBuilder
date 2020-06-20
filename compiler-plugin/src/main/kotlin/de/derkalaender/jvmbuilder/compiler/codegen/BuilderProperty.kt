package de.derkalaender.jvmbuilder.compiler.codegen

import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.resolve.source.getPsi

class BuilderProperty(propertyDesc: PropertyDescriptor) {
  val propName = propertyDesc.name
  val type = propertyDesc.type
  val nullable = type.isMarkedNullable

  // TODO find a way to get the default value directly from PropertyDescriptor
  val defaultValue = (propertyDesc.source.getPsi() as? KtParameter)?.defaultValue

  val optional = nullable || defaultValue != null
}
