package utils.rabbit

fun interface EventProcessor {
    fun process(event: RabbitEvent?)
}