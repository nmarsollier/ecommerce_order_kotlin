package utils.gson

import com.google.gson.*
import com.google.gson.reflect.TypeToken

fun gson(): Gson {
    return GsonBuilder()
        .addSerializationExclusionStrategy(object : ExclusionStrategy {
            override fun shouldSkipField(f: FieldAttributes): Boolean {
                return f.getAnnotation(SkipSerialization::class.java) != null
            }

            override fun shouldSkipClass(clazz: Class<*>?): Boolean {
                return false
            }
        })
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()
}

/**
 * Convert json String to an Object T
 * T can be a list or an objet type
 */
inline fun <reified T> String?.jsonToObject(): T? {
    if (this == null) {
        return null
    }
    return try {
        gson().fromJson(this, object : TypeToken<T>() {}.type)
    } catch (e: JsonSyntaxException) {
        null
    }
}

/**
 * Convert Object to Json
 * T can be a list or an objet type
 */
fun Any?.toJson(): String {
    if (this == null) {
        return ""
    }
    return gson().toJson(this)
}

