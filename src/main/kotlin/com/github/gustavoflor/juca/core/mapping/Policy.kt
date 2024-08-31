package com.github.gustavoflor.juca.core.mapping

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Component

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Component
annotation class Policy(
    @get:AliasFor(annotation = Component::class)
    val value: String = ""
)