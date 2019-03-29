package org.panta.misskey.json

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.panta.misskey.api.network.Connection
import org.panta.misskey.obj.Content
import org.panta.misskey.obj.ContentData
import org.panta.misskey.obj.ContentTypeEnum
import org.panta.misskey.obj.UserData
import java.io.InputStream
import java.net.URL

class TimelineJsonParser{

    //FIXME リファクタリング,改良の必要性有り
    fun timelineParseJson(json: JSONObject): ContentData{
        val user1 = json.getJSONObject("user")
        val reNote = getReNoteJson(json)
        val id = json.getString("id")

        val content: Content
        val contentType: ContentTypeEnum
        if(reNote != null){
            content = Content.ReNote(
                //ReNoteしたユーザーの情報
                getUserData(user1)!!,
                note = Content.Note(
                    noteId = reNote.getString("id"),
                    date = reNote.getString("createdAt"),
                    text = reNote.getString("text"),

                    //ReNoteされたユーザーの情報
                    user = getUserData(reNote.getJSONObject("user"))!!
                ),
                date = json.getString("createdAt")
             )
            contentType = ContentTypeEnum.RE_NOTE
        }else{
            content = Content.Note(
                noteId = json.getString("id"),
                date = json.getString("createdAt"),
                text = json.getString("text"),
                user = getUserData(json.getJSONObject("user"))!!
            )
            contentType = ContentTypeEnum.NOTE
        }
        return ContentData(contentType, id, content)
    }

    fun getTimelineFromArrayJson(jsonArray: JSONArray): List<ContentData>{
        val contentArray = ArrayList<ContentData>()
        for(n in 0.until(jsonArray.length())){
            val obj  = jsonArray.get(n)
            if(obj is JSONObject){
                contentArray.add(timelineParseJson(obj))
            }
        }
        return contentArray
    }
    private fun getUserData(json: JSONObject): UserData?{
        return try{
            UserData(
                name = json.getString("name"),
                userId = json.getString("username"),
                profileImage = getAsyncImage(json.getString("avatarUrl")),
                isCat = json.getString("isCat"),
                isBot = json.getString("isBot"),
                primaryId = json.getString("id"))
        }catch(e: JSONException){
            e.printStackTrace()
            null
        }

    }

    private fun getAsyncImage(url: String): Deferred<Bitmap?> {
        val con = Connection()
        return GlobalScope.async {
            try{
                BitmapFactory.decodeStream(con.get(URL(url)))

            }catch(e: Exception){
                null
            }
        }

    }

    private fun getReNoteJson(json: JSONObject): JSONObject?{
        return try{
            json.getJSONObject("renote")
        }catch(e: JSONException){
            //e.printStackTrace()
            null
        }
    }
}