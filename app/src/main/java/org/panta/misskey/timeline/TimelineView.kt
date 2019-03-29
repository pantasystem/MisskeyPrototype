package org.panta.misskey.timeline

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import kotlinx.android.synthetic.main.fragment_timeline.*
import org.panta.misskey.R
import org.panta.misskey.obj.ContentData

class TimelineView : Fragment(), TimelineContract.View, AbsListView.OnScrollListener{

    override var mPresenter: TimelineContract.Presenter = TimelinePresenter(this)

    lateinit var timelineAdapter: TimelineAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_timeline, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mPresenter.initTimeTimeline()
    }

    override fun onScroll(absListView: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
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
        Handler().post{



        }

    }

    override fun addAllFirstToTimelineList(contentList: List<ContentData>) {
        timelineAdapter.addAllFirst(contentList)
    }

    override fun addAllLastToTimelineList(contentList: List<ContentData>) {
        timelineAdapter.addAllLast(contentList)
    }

    override fun addFirstToTimelineList(content: ContentData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addLastToTimelineList(content: ContentData) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}