package utils.validator

import utils.errors.ValidationError

/**
 * Esta clase ejecuta las validaciones definidas en las interfaces de este mismo paquete.
 * A los atributos de una clase hay que anotarlos con cualquier anotación, luego ejecutar el método validate.
 *
 * Este método va a tirar una excepción si no valida.
 */

@Throws(ValidationError::class)
fun Any.validate() {
    val errors = ValidationError()
    this.validateRequired(errors)
    this.validateMaxLen(errors)
    this.validateMinLen(errors)
    this.validateMaxValue(errors)
    this.validateMinValue(errors)
    if (!errors.isEmpty) {
        throw errors
    }
}

private fun Any.validateRequired(validations: ValidationError) {
    this.javaClass.declaredFields.filter {
        it.getAnnotation(Required::class.java)?.value != null
    }.forEach {
        try {
            it.isAccessible = true
            if (it[this] == null) {
                validations.addPath(it.name, "Es Requerido")
            }
        } catch (e: Exception) {
        }
    }
}

private fun Any.validateMinLen(validations: ValidationError) {
    this.javaClass.declaredFields
        .filter { it.type == String::class.java } //
        .filter {
            it.getAnnotation(MinLen::class.java) != null
        } //
        .forEach {
            try {
                it.isAccessible = true
                val minLen = (it.getAnnotation(MinLen::class.java) as MinLen).value
                val value = it[this] as String
                if (value.length < minLen) {
                    validations.addPath(it.name, "Mínimo reqerido $minLen")
                }
            } catch (e: Exception) {
            }
        }
}

private fun Any.validateMaxLen(validations: ValidationError) {
    this.javaClass.declaredFields
        .filter { it.type == String::class.java }
        .filter { it.getAnnotation(MaxLen::class.java) != null }
        .forEach {
            try {
                it.isAccessible = true
                val maxLen = (it.getAnnotation(MaxLen::class.java) as MaxLen).value
                val value = it[this] as String
                if (value.length > maxLen) {
                    validations.addPath(it.name, "Máximo permitido  $maxLen")
                }
            } catch (e: Exception) {
            }
        }
}

private fun Any.validateMinValue(validations: ValidationError) {
    this.javaClass.declaredFields
        .filter { it.getAnnotation(MinValue::class.java) != null }
        .forEach {
            try {
                it.isAccessible = true
                val value = it.getDouble(this)
                val minValue = (it.getAnnotation(MinValue::class.java) as MinValue).value
                if (value < minValue) {
                    validations.addPath(it.name, "Mínimo reqerido $minValue")
                }
            } catch (e: Exception) {
            }
        }
}

private fun Any.validateMaxValue(validations: ValidationError) {
    this.javaClass.declaredFields
        .filter { it.getAnnotation(MaxValue::class.java) != null }
        .forEach {
            try {
                it.isAccessible = true
                val value = it.getDouble(this)
                val maxValue = (it.getAnnotation(MaxValue::class.java) as MaxValue).value
                if (value > maxValue) {
                    validations.addPath(it.name, "Máximo permitido  $maxValue")
                }
            } catch (e: Exception) {
            }
        }
}
