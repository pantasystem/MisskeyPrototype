package org.panta.misskey.timeline

import android.util.Log
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.panta.misskey.api.NotesConnection
import java.util.*

class TimelinePresenter(private val mView: TimelineContract.View) : TimelineContract.Presenter{

    init{
        apiCounterReset()
    }

    private var mThisInstancesChannelId: String? = null
    private val mNotesConnect = NotesConnection("!Pap6YHn60rmhbwTV81YJKWkMIoX2GKy8")
    //timelineListはArrayAdapterが管理をする

    //APIを叩いた数を計測する
    private var mRequestedAPICounter = 0
    //一定時間ごとにAPIカウンターをリセットする
    private fun apiCounterReset(){
        GlobalScope.launch{
            while(true){
                mRequestedAPICounter = 0
                delay(500)
            }
        }
    }

    override fun connectChannel(id: String) {

    }

    override fun disconnectChannel(id: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            val contentData = mNotesConnect.getNotesFromId(noteId, NotesConnection.SinceOrUntil.SINCE, 10)
            if(contentData == null){
                mView.onError("タイムライン取得中にエラー発生")
            }else{
                Log.d("ContentData", contentData.toString())
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
            val contentData = mNotesConnect.getNotesFromId(noteId, NotesConnection.SinceOrUntil.UNTIL, 10)
            if(contentData == null){
                mView.onError("タイムライン取得中にエラー発生")
            }else{
                Log.d("ContentData", contentData.toString())
                mView.addAllLastToTimelineList(contentData)
            }
        }
    }

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}