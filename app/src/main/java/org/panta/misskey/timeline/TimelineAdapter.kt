package org.panta.misskey.timeline

import android.content.Context
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.timeline_item.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.panta.misskey.R
import org.panta.misskey.obj.Content
import org.panta.misskey.obj.ContentData
import org.panta.misskey.obj.ContentTypeEnum
import java.lang.Exception

class TimelineAdapter(private val context: Context, private val layoutId: Int,private val timelineList: List<ContentData>,private val activity: FragmentActivity) : BaseAdapter(){

    data class ViewHolder(var userIcon: ImageView, var userName: TextView, var userId: TextView, var text: TextView,
                          var reNoteUserIcon:ImageView, var reNoteUserName: TextView, var reNoteUserGaReNote: TextView)
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
            holder = ViewHolder(
                convertView!!.user_icon_view,
                convertView.userNameView,
                convertView.user_id_view,
                convertView.noteTextView,
                convertView.reNoteIcon,
                convertView.reNoteUserName,
                convertView.displayReNote
            )
            convertView.tag = holder
        }else{
            holder = convertView.tag as ViewHolder
        }

        val content = timelineList[p0]
        when(content.contentType){
            ContentTypeEnum.NOTE ->{
                val note = content.content as Content.Note
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

            }
            ContentTypeEnum.RE_NOTE -> {
                val reNote = content.content as Content.ReNote
                holder.text.text = reNote.note.text
                holder.userName.text = reNote.note.user.name
                holder.userId.text = reNote.note.user.userId

                holder.reNoteUserIcon.visibility = View.VISIBLE
                holder.reNoteUserName.visibility = View.VISIBLE
                holder.reNoteUserGaReNote.visibility = View.VISIBLE

                holder.reNoteUserName.text = reNote.reNoteUser.name
                Log.d("TimelineAdapter", "RenoteUserName ${reNote.reNoteUser.name}")

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
                //Log.d("ListView", "Adapter実行中")

            }
        }

        return convertView

    }

    fun addFirst(content: ContentData){
        activity.runOnUiThread {
            if(timelineList is ArrayList){
                timelineList.add(0, content)
                notifyDataSetChanged()
            }

        }
    }

    fun addLast(content: ContentData){
        activity.runOnUiThread{
            if(timelineList is ArrayList){
                timelineList.add(content)
                notifyDataSetChanged()
            }
        }
    }

    fun addAllFirst(list: List<ContentData>){
        activity.runOnUiThread {
            if(timelineList is ArrayList){
                timelineList.addAll(0, list)
                notifyDataSetChanged()
            }

        }
    }

    fun addAllLast(list: List<ContentData>){
        activity.runOnUiThread{
            if(timelineList is ArrayList){
                timelineList.addAll(list)
                notifyDataSetChanged()
            }

        }
    }


}