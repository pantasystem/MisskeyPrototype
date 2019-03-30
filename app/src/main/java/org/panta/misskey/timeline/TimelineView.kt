package org.panta.misskey.timeline

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.java_websocket.client.WebSocketClient
import org.panta.misskey.R
import org.panta.misskey.obj.ContentData
import org.panta.misskey.streaming.StreamingPort
import java.net.URI


class TimelineView : Fragment(), TimelineContract.View, AbsListView.OnScrollListener{

    private val mWebSocket = StreamingPort.getInstance(URI("wss://misskey.xyz/streaming?i=!Pap6YHn60rmhbwTV81YJKWkMIoX2GKy8"))

    override var mPresenter: TimelineContract.Presenter = TimelinePresenter(this, mWebSocket!!)

    lateinit var timelineAdapter: TimelineAdapter

    private var mListViewFirstVisibleItemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(mWebSocket is WebSocketClient){
            mWebSocket.connect()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(org.panta.misskey.R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPresenter.initTimeTimeline()
    }

    override fun onStart(){
        super.onStart()

        mPresenter.connectChannel()
    }

    override fun onStop(){
        super.onStop()

        mPresenter.disconnectChannel()
    }

    override fun onScroll(absListView: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        mListViewFirstVisibleItemPosition = firstVisibleItem

        if(firstVisibleItem < 1){
            val contentData = timelineAdapter.getItem(0) as ContentData
            mPresenter.getNoteSinceId(contentData.id)
        }

        val lastIndex = firstVisibleItem + visibleItemCount
        if(lastIndex >= totalItemCount){
            val contentData = timelineAdapter.getItem(lastIndex - 1) as ContentData
            mPresenter.getNoteUntilId(contentData.id)
        }
    }

    override fun onScrollStateChanged(p0: AbsListView?, p1: Int) {
    }

    override fun initTimeline(contentList: List<ContentData>) {
        listViewSetAdapter(contentList)
    }

    private fun listViewSetAdapter(list: List<ContentData>){
        try{
            activity?.runOnUiThread{
                timelineAdapter = TimelineAdapter(context!!, R.layout.timeline_item, list, activity!!)
                timelineListView.adapter = timelineAdapter
                timelineListView.setOnScrollListener(this)
            }

        }catch (e: Exception){
            e.printStackTrace()
        }

    }

    override fun addAllFirstToTimelineList(contentList: List<ContentData>) {
        timelineAdapter.addAllFirst(contentList)

        activity?.runOnUiThread{
            timelineListView.setSelectionFromTop(contentList.size, 0)
        }
    }

    override fun addAllLastToTimelineList(contentList: List<ContentData>) {
        timelineAdapter.addAllLast(contentList)
    }

    override fun addFirstToTimelineList(content: ContentData) {
        val nowPosition = timelineListView.firstVisiblePosition
        Log.d("AddItemToListView", "nowPosition:$nowPosition")

        //先頭を表示していない場合
        if(nowPosition > 0){
            timelineAdapter.addFirst(content, false)
            /*activity?.runOnUiThread{
                timelineListView.setSelectionFromTop(mListViewFirstVisibleItemPosition + 1,0)
            }*/
        }else{
            timelineAdapter.addFirst(content, true)
        }
    }

    override fun addLastToTimelineList(content: ContentData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}