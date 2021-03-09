package rabbit

class Consumers private constructor() {
    companion object {
        fun init() {
            ConsumeArticleData.init()
            ConsumeAuthLogout.init()
            ConsumePlaceOrder.init()
        }
    }
}