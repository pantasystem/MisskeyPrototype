package org.panta.misskey.timeline

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.panta.misskey.api.NotesConnection
import org.panta.misskey.json.TimelineJsonParser
import org.panta.misskey.streaming.IStreamingPortable
import java.net.URL
import java.util.*

class TimelinePresenter(private val mView: TimelineContract.View, private val mStreamingSocket: IStreamingPortable) : TimelineContract.Presenter, IStreamingPortable.MessageListener{

    private val mNotesConnect = NotesConnection("!Pap6YHn60rmhbwTV81YJKWkMIoX2GKy8", URL("https://misskey.xyz/api/notes/timeline"))

    private var mChannelId = 0

    //APIを叩いた数を計測する
    private var mRequestedAPICounter = 0
        set(value){
            field = value
            GlobalScope.launch{
                //一定時間ごとにAPIカウンターをリセットする
                delay(500)
                field = 0
            }
        }

    override fun connectChannel() {
        Log.d("TimelinePresenter", "ConnectChannelメソッド実行")
        val randomId = Random().nextInt(10000)
        mChannelId = randomId
        mStreamingSocket.createChannel("homeTimeline", randomId.toString(), this)
    }

    override fun disconnectChannel() {
        mStreamingSocket.destroyChannel(mChannelId.toString())
    }

    override fun initTimeTimeline() {
        GlobalScope.launch {
            try{
                val contentData = mNotesConnect.getNotesFromDate(Date(), NotesConnection.SinceOrUntil.UNTIL, 10)
                if(contentData == null){
                    mView.onError("タイムライン取得中にエラー発生")
                }else{
                    mView.initTimeline(contentData)
                }
            }catch(e: Exception){
                e.printStackTrace()
            }

        }
    }

    override fun getNoteSinceId(noteId: String) {
        if(mRequestedAPICounter > 0){
            return
        }else{
            mRequestedAPICounter++
        }
        GlobalScope.launch{
            val contentData = mNotesConnect.getNotesFromId(noteId, NotesConnection.SinceOrUntil.SINCE, 15)
            if(contentData == null){
                mView.onError("タイムライン取得中にエラー発生")
            }else if(contentData.isNotEmpty()){
                //Log.d("ContentData", contentData.toString())
                mView.addAllFirstToTimelineList(contentData)
            }
        }
    }

    override fun getNoteUntilId(noteId: String) {
        if(mRequestedAPICounter > 0){
            return
        }else{
            mRequestedAPICounter++
        }
        GlobalScope.launch{
            val contentData = mNotesConnect.getNotesFromId(noteId, NotesConnection.SinceOrUntil.UNTIL, 15)
            if(contentData == null){
                mView.onError("タイムライン取得中にエラー発生")
            }else{
                //Log.d("ContentData", contentData.toString())
                mView.addAllLastToTimelineList(contentData)
            }
        }
    }

    override fun onMessage(json: JSONObject) {
        val timelineJsonParser = TimelineJsonParser()
        val contentData = timelineJsonParser.timelineParseJson(json)

        Log.d("onMessage", "Messageを受信した")
        mView.addFirstToTimelineList(contentData)
        mRequestedAPICounter++
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}