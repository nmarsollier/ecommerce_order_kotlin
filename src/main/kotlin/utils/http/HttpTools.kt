package utils.http

import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder

class HttpTools {
    private fun httpClient() = HttpClientBuilder.create().build()

    fun get(url: String, headers: List<Pair<String, String>>): CloseableHttpResponse {
        return httpClient().execute(
            HttpGet(url).apply {
                headers.forEach {
                    addHeader(it.first, it.second)
                }
            }
        )
    }

    companion object {
        private var currentInstance: HttpTools? = null

        fun instance(): HttpTools {
            return currentInstance ?: HttpTools().also {
                currentInstance = it
            }
        }
    }
}