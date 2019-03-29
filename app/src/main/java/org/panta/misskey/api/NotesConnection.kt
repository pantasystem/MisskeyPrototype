package org.panta.misskey.api

import android.util.Log

import org.json.JSONArray
import org.json.JSONObject
import org.panta.misskey.api.network.Connection
import org.panta.misskey.json.TimelineJsonParser
import org.panta.misskey.obj.Content
import org.panta.misskey.obj.ContentData
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.URL
import java.util.*

class NotesConnection(private val authKey: String){

    enum class SinceOrUntil{
        SINCE,UNTIL
    }

    private val mConnection = Connection()
    private val timelineParser = TimelineJsonParser()


    suspend fun getNotesFromId(id: String, getType:SinceOrUntil, limit: Int,
                               includeMyReNote: Boolean = true, includeRenotedMyNotes: Boolean = true, includeLocalRenotes: Boolean = true): List<ContentData>? {
        val json = JSONObject().apply {
            put("i", authKey)
            put("limit", limit)

            if (getType == SinceOrUntil.UNTIL) {
                put("untilId", id)
            } else {
                put("sinceId", id)
            }
            put("includeMyRenotes", includeMyReNote)
            put("includeRenotedMyNotes", includeRenotedMyNotes)
            put("includeLocalRenotes", includeLocalRenotes)

        }.toString()
        val content = getTextContent(json.toString())
        return if(content == null){
            Log.d("Notes", "ネットワークアクセスしたもののNullが返ってきた")
            return null
        }else{
            convertToJsonObjectFromText(content)
        }

    }

    //private fun createJsonObjToGetNotesFromDate
    suspend fun getNotesFromDate
                (date: Date,
                 getType:SinceOrUntil,
                 limit: Int,
                 includeMyReNote: Boolean = true,
                 includeRenotedMyNotes: Boolean = true,
                 includeLocalRenotes: Boolean = true): List<ContentData>? {
        val json = JSONObject().apply {
            put("i", authKey)
            put("limit", limit)

            if (getType == SinceOrUntil.UNTIL) {
                put("untilId", date.time)
            } else {
                put("sinceDate", date.time)
            }
            put("includeMyRenotes", includeMyReNote)
            put("includeRenotedMyNotes", includeRenotedMyNotes)
            put("includeLocalRenotes", includeLocalRenotes)

        }

        val content = getTextContent(json.toString())
        return if(content == null){
            Log.d("Notes", "ネットワークアクセスしたもののNullが返ってきた")
            return null
        }else{
            convertToJsonObjectFromText(content)
        }


    }


    private suspend fun getTextContent(json: String): String?{
        val inputStream = mConnection.asyncPost(URL("https://misskey.xyz/api/notes/timeline"),json )
        inputStream?: return null

        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(inputStream))
        while(true){
            val s:String? = reader.readLine()
            if(s == null){
                break
            }else{
                sb.append(s)
            }
        }

        return sb.toString()

    }

    private fun convertToJsonObjectFromText(json: String): List<ContentData>{
        return timelineParser.getTimelineFromArrayJson(JSONArray(json))

    }
}