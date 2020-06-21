package de.derkalaender.jvmbuilder.compiler.codegen

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.descriptors.CallableMemberDescriptor
import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SimpleFunctionDescriptor
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.descriptors.annotations.Annotations
import org.jetbrains.kotlin.descriptors.impl.SimpleFunctionDescriptorImpl
import org.jetbrains.kotlin.extensions.AnnotationBasedExtension
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.descriptorUtil.builtIns
import org.jetbrains.kotlin.resolve.extensions.SyntheticResolveExtension
import org.jetbrains.kotlin.resolve.source.getPsi

class JvmBuilderSyntheticResolveExtension(
    private val messageCollector: MessageCollector, private val fqJvmBuilderAnnotation: FqName
) : SyntheticResolveExtension, AnnotationBasedExtension {
  override fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?) =
      listOf(fqJvmBuilderAnnotation.toString())

  override fun generateSyntheticMethods(
      thisDescriptor: ClassDescriptor,
      name: Name,
      bindingContext: BindingContext,
      fromSupertypes: List<SimpleFunctionDescriptor>,
      result: MutableCollection<SimpleFunctionDescriptor>
  ) {
    super.generateSyntheticMethods(thisDescriptor, name, bindingContext, fromSupertypes, result)

    if (name.asString() != "test") return
    if (!thisDescriptor.hasSpecialAnnotation(null)) return

    println(name)

    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "@JvmBuilder > Processing annotated class ${thisDescriptor.name}",
        MessageUtil.psiElementToMessageLocation(thisDescriptor.source.getPsi()))

    if (!thisDescriptor.isData) {
      messageCollector.report(
          CompilerMessageSeverity.ERROR,
          "@JvmBuilder > Only data classes are supported!",
          MessageUtil.psiElementToMessageLocation(thisDescriptor.source.getPsi()))
      return
    }

    result +=
        SimpleFunctionDescriptorImpl.create(
            thisDescriptor,
            Annotations.EMPTY,
            name,
            CallableMemberDescriptor.Kind.SYNTHESIZED,
            thisDescriptor.source)
            .initialize(
                null,
                thisDescriptor.thisAsReceiverParameter,
                emptyList(),
                emptyList(),
                thisDescriptor.builtIns.stringType,
                Modality.OPEN,
                Visibilities.PUBLIC)
  }

  override fun getSyntheticFunctionNames(thisDescriptor: ClassDescriptor) =
      if (thisDescriptor.hasSpecialAnnotation(null) && thisDescriptor.isData)
          listOf(Name.identifier("test"))
      else emptyList()
}
