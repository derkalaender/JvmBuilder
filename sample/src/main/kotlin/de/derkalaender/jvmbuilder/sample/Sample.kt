package de.derkalaender.jvmbuilder.sample

import de.derkalaender.jvmbuilder.annotations.JvmBuilder

@JvmBuilder
data class Sample(val sampleValue: String = "")
