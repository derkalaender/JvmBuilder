package de.derkalaender.jvmbuilder.compiler

import de.derkalaender.jvmbuilder.annotations.JvmBuilder

@JvmBuilder
data class Example(
  var requiredProp: Int,
  var nullableProp: Boolean?,
  val defaultProp: String = "someValue",
  val defaultNullProp: String? = "shouldNotBeNull",
  val defaultActualNullProp: String? = null
) {
  companion object {
    @JvmStatic
    fun builder(requiredProp: Int) = ExampleBuilder(requiredProp)
  }

  class ExampleBuilder(private val requiredProp: Int) {
    private var nullableProp: Boolean? = null
    private var defaultProp: String = "someValue"
    private var defaultNullProp: String? = "shouldNotBeNull"
    private var defaultActualNullProp: String? = null

    fun nullableProp(value: Boolean?): ExampleBuilder {
      this.nullableProp = value
      return this
    }

    fun defaultProp(value: String): ExampleBuilder {
      this.defaultProp = value
      return this
    }

    fun defaultNullProp(value: String?): ExampleBuilder {
      this.defaultNullProp = value
      return this
    }

    fun defaultActualNullProp(value: String?): ExampleBuilder {
      this.defaultActualNullProp = value
      return this
    }

    fun build() =
      Example(requiredProp, nullableProp, defaultProp, defaultNullProp, defaultActualNullProp)
  }
}
