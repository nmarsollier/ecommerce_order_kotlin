package rabbit

class Consumers(
    private val consumeArticleData: ConsumeArticleData,
    private val consumeAuthLogout: ConsumeAuthLogout,
    private val consumePlaceOrder: ConsumePlaceOrder
) {
    fun init() {
        consumeArticleData.init()
        consumeAuthLogout.init()
        consumePlaceOrder.init()
    }
}