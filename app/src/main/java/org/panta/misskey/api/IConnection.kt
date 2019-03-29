package org.panta.misskey.api

import java.io.InputStream
import java.net.URL

interface IConnection {
    fun post(url: URL, json: String): InputStream?//コールバックなど必要
    fun get(url: URL): InputStream?

    suspend fun asyncPost(url: URL, json: String): InputStream?
}