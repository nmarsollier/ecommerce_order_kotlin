package utils.kt


fun <T> Iterator<T>.nextOrNull(): T? {
    return if (this.hasNext()) {
        this.next()
    } else null
}