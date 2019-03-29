package org.panta.misskey.api.network

import android.util.Log
import org.panta.misskey.api.IConnection
import java.io.BufferedInputStream
import java.io.InputStream
import java.io.PrintStream
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class Connection : IConnection {

    override fun post(url: URL, json: String): InputStream? {
        return try{
            val con = (url.openConnection() as HttpsURLConnection).apply{
                requestMethod = "POST"
                instanceFollowRedirects = true
                doOutput = true

                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                val os = this.outputStream
                val ps = PrintStream(os)
                ps.print(json)
                ps.close()


            }
            BufferedInputStream(con.inputStream)

        }catch(e : Exception){
            Log.e("Connection", "POSTリクエストが失敗してしまいましたNULLを返します", e)
            null
        }
    }

    override fun get(url: URL): InputStream? {
        return try{
            val con = (url.openConnection() as HttpsURLConnection).apply{
                requestMethod = "GET"

                connect()

            }
            Log.d("画像URL", url.toString())
            BufferedInputStream(con.inputStream)

        }catch (e: java.lang.Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun asyncPost(url: URL, json: String): InputStream? {
        return post(url, json)

    }
}