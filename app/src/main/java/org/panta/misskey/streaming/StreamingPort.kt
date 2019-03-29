package org.panta.misskey.streaming

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import java.util.*

//PresenterからはIStreamingPortableとして扱う
//Activity, FragmentからはWebSocketClientとして扱い接続などを担当
//ライフサイクルの伝達はPresenterを通して行う
//onStartでチャンネルに接続しonStopでチャンネルを削除する
//onDestroyはすべてが無に返るので基本的に気にしないこととする
class StreamingPort private constructor(uri: URI) : IStreamingPortable{

    companion object{
        private var mStreamingInstance: IStreamingPortable? = null
        fun getInstance(uri: URI): IStreamingPortable?{
            StreamingPort(uri)
            if(mStreamingInstance == null){
                mStreamingInstance = StreamingPort(uri)
                //return mStreamingInstance
            }
            return mStreamingInstance
        }
    }

    //最悪配列で管理しある一定の段階でonClose済みのWebSocketから削除していく
    private val mSocket = WebSocket(uri)
    init{
        mSocket.connect()
    }

    private val mConnectToChannelQueue = ArrayDeque<String>()
    private val mChannelAndIdMap = HashMap<String, IStreamingPortable.MessageListener>()

    override fun createChannel(channel: String, id: String, listener: IStreamingPortable.MessageListener) {
        val jsonString = "{\"type\": \"connect\", \"body\": {\"channel\": \"$channel\", \"id\": \"$id\"}}"
        if(mSocket.isOpen){
           mSocket.send(jsonString)
        }else{
            mConnectToChannelQueue.add(jsonString)
        }
    }

    override fun destroyChannel(id: String) {
        mChannelAndIdMap.remove(id)
    }

    override fun allClearChannel() {
        mChannelAndIdMap.clear()
    }
    private inner class WebSocket(uri: URI): WebSocketClient(uri){
        override fun onOpen(handshakedata: ServerHandshake?) {
            while(mConnectToChannelQueue.isNotEmpty()){
                this.send(mConnectToChannelQueue.removeFirst())
            }
        }

        override fun onMessage(message: String?) {
            val jsonObj = JSONObject(message)
            if(jsonObj.getString("type") == "channel"){
                val body1 = jsonObj.getJSONObject("body")
                val id = body1.getString("id")
                mChannelAndIdMap.filterKeys {
                    it == id
                }.forEach{
                    it.value.onMessage(body1.getJSONObject("body"))
                }
            }
        }

        override fun onError(ex: Exception?) {
            Log.e("WebSocket", "onError　WebSocketでエラー発生", ex)
        }

        override fun onClose(code: Int, reason: String?, remote: Boolean) {
            Log.d("WebSocket", "WebSocketはCloseしました code:$code, reason:$reason, remote:$remote")
        }
    }



}