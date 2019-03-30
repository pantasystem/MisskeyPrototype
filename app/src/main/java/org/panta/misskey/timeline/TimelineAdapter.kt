package org.panta.misskey.timeline

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.timeline_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.panta.misskey.R
import org.panta.misskey.createNote.CreateNoteActivity
import org.panta.misskey.obj.Content
import org.panta.misskey.obj.ContentData
import org.panta.misskey.obj.ContentTypeEnum
import java.lang.Exception

class TimelineAdapter(private val context: Context, private val layoutId: Int,private val timelineList: List<ContentData>,private val activity: FragmentActivity) : BaseAdapter(){

    data class ViewHolder(var userIcon: ImageView, var userName: TextView, var userId: TextView, var text: TextView,
                          var reNoteUserIcon:ImageView, var reNoteUserName: TextView, var reNoteUserGaReNote: TextView, var reactionHolder: ReactionViewHolder)

    data class ReactionViewHolder(var replyButton: ImageButton, var replyCountView: TextView, var reNoteButton: ImageButton, var reNoteCounterView: TextView,
                                  var reactionButton: ImageButton, var descriptionButton: ImageButton)
    private val inflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return timelineList.size
    }

    override fun getItem(p0: Int): Any {
        return timelineList[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var convertView = p1
        val holder: ViewHolder

        if(convertView == null){
            convertView = inflater.inflate(layoutId, null)
            val reactionHolder = ReactionViewHolder(
                convertView!!.replyButton,
                convertView.replyCountView,
                convertView.reNoteButton,
                convertView.reNoteCountView,
                convertView.reactionButton,
                convertView.descriptionButton
            )
            holder = ViewHolder(
                convertView.user_icon_view,
                convertView.userNameView,
                convertView.user_id_view,
                convertView.noteTextView,
                convertView.reNoteIcon,
                convertView.reNoteUserName,
                convertView.displayReNote,
                reactionHolder
            )
            convertView.tag = holder
        }else{
            holder = convertView.tag as ViewHolder
        }

        val content = timelineList[p0]
        when(content.contentType){
            ContentTypeEnum.NOTE ->{
                onNote(holder, content)
            }
            ContentTypeEnum.RE_NOTE -> {
                onReNote(holder, content)
            }
        }

        return convertView

    }

    private fun reNote(id: String){
        val intent = Intent(context, CreateNoteActivity::class.java)
        intent.putExtra(CreateNoteActivity.TARGET_NOTE_ID, id)
        intent.putExtra(CreateNoteActivity.NOTE_TYPE, CreateNoteActivity.NOTE_TYPE_RENOTE)
        context.startActivity(intent)
    }

    private fun replay(id: String){
        val intent = Intent(context, CreateNoteActivity::class.java)
        intent.putExtra(CreateNoteActivity.TARGET_NOTE_ID, id)
        intent.putExtra(CreateNoteActivity.NOTE_TYPE, CreateNoteActivity.NOTE_TYPE_REPLY)
        context.startActivity(intent)
    }

    private fun onReNote(holder: ViewHolder, content: ContentData){
        val reNote = content.content as Content.ReNote
        holder.text.text = reNote.note.text
        holder.userName.text = reNote.note.user.name
        holder.userId.text = reNote.note.user.userId

        holder.reNoteUserIcon.visibility = View.VISIBLE
        holder.reNoteUserName.visibility = View.VISIBLE
        holder.reNoteUserGaReNote.visibility = View.VISIBLE


        holder.reNoteUserName.text = reNote.reNoteUser.name
        //Log.d("TimelineAdapter", "RenoteUserName ${reNote.reNoteUser.name}")

        GlobalScope.launch{
            try{
                val noteUserBitmap = reNote.note.user.profileImage.await()
                val reNoteUserBitmap = reNote.reNoteUser.profileImage.await()
                activity.runOnUiThread{
                    holder.userIcon.setImageBitmap(noteUserBitmap)
                    holder.reNoteUserIcon.setImageBitmap(reNoteUserBitmap)

                }

            }catch(e: Exception){
                e.printStackTrace()
            }
        }

        //引用リノートかそうでないかを判別する必要あり
        holder.reactionHolder.reNoteButton.setOnClickListener {
            reNote(reNote.note.noteId)
        }

        holder.reactionHolder.replyButton.setOnClickListener{
            reNote(reNote.note.noteId)

        }
        //Log.d("ListView", "Adapter実行中")
    }



    private fun onNote(holder: ViewHolder, content: ContentData){
        val note = content.content as Content.Note
        holder.reNoteUserName.visibility = View.GONE
        holder.reNoteUserIcon.visibility = View.GONE
        holder.reNoteUserGaReNote.visibility = View.GONE

        holder.text.text = note.text
        holder.userId.text = note.user.userId
        holder.userName.text = note.user.name
        holder.userIcon.setImageResource(R.drawable.human_icon)
        GlobalScope.launch{
            try{
                val bitmap = note.user.profileImage.await()

                activity.runOnUiThread {
                    holder.userIcon.setImageBitmap(bitmap)
                }

            }catch(e : Exception){
                e.printStackTrace()
            }
        }

        holder.reactionHolder.reNoteButton.setOnClickListener {
            reNote(note.noteId)

        }

        holder.reactionHolder.replyButton.setOnClickListener{
            replay(note.noteId)
        }


    }

    fun addFirst(content: ContentData, isUpdate: Boolean){
        activity.runOnUiThread {
            if(timelineList is ArrayList && duplicateCheck(content)){
                timelineList.add(0, content)
                if(isUpdate){
                    activity.runOnUiThread {
                        notifyDataSetChanged()
                    }
                }
            }

        }
    }

    fun addLast(content: ContentData){
        activity.runOnUiThread{
            if(timelineList is ArrayList && duplicateCheck(content)){
                timelineList.add(content)
                notifyDataSetChanged()
            }
        }
    }

    fun addAllFirst(list: List<ContentData>){
        val checkedList = duplicateCheckAndRemoveContent(list)
        if(checkedList.isEmpty()){
            Log.d("TimelineAdapter", "addAllFirst　リストが空だから更新しない")
            return
        }
        activity.runOnUiThread {
            if(timelineList is ArrayList){
                timelineList.addAll(0, checkedList)
                notifyDataSetChanged()
            }

        }
    }

    fun addAllLast(list: List<ContentData>){
        val checkedList = duplicateCheckAndRemoveContent(list)
        if(list.isEmpty()) return
        activity.runOnUiThread{
            if(timelineList is ArrayList){
                timelineList.addAll(checkedList)
                notifyDataSetChanged()
            }

        }
    }

    private fun duplicateCheck(contentData: ContentData): Boolean{
        val count = timelineList.filter{
            it.id == contentData.id
        }.size
        return count < 1
    }

    private fun duplicateCheckAndRemoveContent(contentDataList: List<ContentData>): List<ContentData>{
        return contentDataList.filter{
            duplicateCheck(it)
        }
    }





}