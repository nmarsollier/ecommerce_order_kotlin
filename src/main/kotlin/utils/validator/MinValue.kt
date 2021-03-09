package utils.validator

/**
 * Validador que valida tamaño mínimo de un numero en una propiedad de una clase
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class MinValue(val value: Int)