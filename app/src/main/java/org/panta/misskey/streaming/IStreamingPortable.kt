package org.panta.misskey.streaming

import org.json.JSONObject

interface IStreamingPortable{
    interface MessageListener {
        fun onMessage(json: JSONObject)
    }

    fun createChannel(channel: String, id: String, listener: MessageListener)
    fun destroyChannel(id: String)
    fun allClearChannel()
}