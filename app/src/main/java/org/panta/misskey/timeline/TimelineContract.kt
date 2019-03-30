package org.panta.misskey.timeline

import org.panta.misskey.BasePresenter
import org.panta.misskey.BaseView
import org.panta.misskey.obj.ContentData

interface TimelineContract{

    interface View : BaseView<Presenter>{
        fun initTimeline(contentList: List<ContentData>)
        fun addFirstToTimelineList(content: ContentData)
        fun addLastToTimelineList(content: ContentData)
        fun addAllFirstToTimelineList(contentList: List<ContentData>)
        fun addAllLastToTimelineList(contentList: List<ContentData>)
        fun onError(msg: String)
    }

    interface Presenter: BasePresenter{

        fun connectChannel()
        fun disconnectChannel()

        fun getNoteUntilId(noteId: String)
        fun getNoteSinceId(noteId: String)
        fun initTimeTimeline()


    }
}