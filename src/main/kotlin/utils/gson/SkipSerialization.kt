package utils.gson

/**
 * Los campos anotados no se van a serializar como json
 */
@Target(AnnotationTarget.FIELD)
annotation class SkipSerialization 