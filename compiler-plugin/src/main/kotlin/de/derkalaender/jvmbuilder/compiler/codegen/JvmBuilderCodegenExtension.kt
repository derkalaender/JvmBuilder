package de.derkalaender.jvmbuilder.compiler.codegen

import org.jetbrains.kotlin.backend.jvm.codegen.ClassCodegen
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.PropertyDescriptor
import org.jetbrains.kotlin.descriptors.impl.VariableDescriptorWithInitializerImpl
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.source.getPsi

class JvmBuilderCodegenExtension(
    private val messageCollector: MessageCollector, private val fqJvmBuilderAnnotation: FqName
) : ExpressionCodegenExtension {
  override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
    val targetClass = codegen.descriptor

    if (!targetClass.isJvmBuilderApplicable(fqJvmBuilderAnnotation, messageCollector)) return

    val constructor = targetClass.constructors.firstOrNull { it.isPrimary } ?: return
    val properties =
        constructor.valueParameters.mapNotNull {
          codegen.bindingContext.get(BindingContext.VALUE_PARAMETER_AS_PROPERTY, it)
        }

    codegen.context

    BuilderClassGenerator(
        declaration = codegen.myClass as KtClassOrObject, classDescriptor = targetClass)
        .generateBuilderClass(properties)
  }
}

private class BuilderClassGenerator(
    private val declaration: KtClassOrObject, private val classDescriptor: ClassDescriptor
) {
  fun generateBuilderClass(properties: List<PropertyDescriptor>) {
    val constructorProperties =
        properties.filter { (it.source.getPsi() as KtParameter).hasDefaultValue() }
    val defaultHavingProperties = properties - constructorProperties
  }
}
