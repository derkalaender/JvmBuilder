package de.derkalaender.jvmbuilder.compiler.codegen

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.messages.MessageUtil
import org.jetbrains.kotlin.codegen.FunctionCodegen
import org.jetbrains.kotlin.codegen.ImplementationBodyCodegen
import org.jetbrains.kotlin.codegen.extensions.ExpressionCodegenExtension
import org.jetbrains.kotlin.extensions.AnnotationBasedExtension
import org.jetbrains.kotlin.incremental.components.NoLookupLocation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtModifierListOwner
import org.jetbrains.kotlin.resolve.BindingContext
import org.jetbrains.kotlin.resolve.jvm.AsmTypes
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.source.getPsi
import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.commons.InstructionAdapter

class JvmBuilderCodegenExtension(
    private val messageCollector: MessageCollector, private val fqJvmBuilderAnnotation: FqName
) : ExpressionCodegenExtension, AnnotationBasedExtension {
  override fun getAnnotationFqNames(modifierListOwner: KtModifierListOwner?) =
      listOf(fqJvmBuilderAnnotation.toString())

  override fun generateClassSyntheticParts(codegen: ImplementationBodyCodegen) {
    val targetClass = codegen.descriptor

    if (!targetClass.hasSpecialAnnotation(null)) return

    messageCollector.report(
        CompilerMessageSeverity.INFO,
        "@JvmBuilder > Processing annotated class ${targetClass.name}",
        MessageUtil.psiElementToMessageLocation(targetClass.source.getPsi()))

    if (!targetClass.isData) {
      messageCollector.report(
          CompilerMessageSeverity.ERROR,
          "@JvmBuilder > Only data classes are supported!",
          MessageUtil.psiElementToMessageLocation(targetClass.source.getPsi()))
      return
    }

    val constructor = targetClass.constructors.firstOrNull { it.isPrimary } ?: return
    val properties =
        constructor.valueParameters
            .mapNotNull {
              codegen.bindingContext.get(BindingContext.VALUE_PARAMETER_AS_PROPERTY, it)
            }
            .map { BuilderProperty(it) }

    val builder = codegen.v

    val function =
        targetClass.unsubstitutedMemberScope
            .getContributedFunctions(Name.identifier("test"), NoLookupLocation.FROM_BACKEND)
            .firstOrNull()

    println(function?.name)

    // val typemapper = codegen.state.typeMapper
    // val outerName = typemapper.classInternalName(targetClass)
    //
    // val innerName = "${targetClass.name}Builder"
    //
    // val combinedName = "$outerName$$innerName"
    //
    // public static final class *Outer*Builder
    // builder.visitInnerClass(
    //   combinedName,
    //   outerName,
    //   innerName,
    //   Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC or Opcodes.ACC_FINAL
    // )

    val init =
        builder.newMethod(
            JvmDeclarationOrigin.NO_ORIGIN,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_OPEN,
            "test",
            "()Ljava/lang/String;",
            null,
            null)
            .let(::InstructionAdapter)

    init.visitCode()

    init.visitLdcInsn("Hello")
    init.areturn(AsmTypes.JAVA_STRING_TYPE)

    FunctionCodegen.endVisit(init, "test", codegen.myClass)
  }
}
