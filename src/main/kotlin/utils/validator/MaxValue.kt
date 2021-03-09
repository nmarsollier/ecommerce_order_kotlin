package utils.validator

/**
 * Validador que valida tamaño máximo de un numero en una propiedad de una clase
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class MaxValue(val value: Int)