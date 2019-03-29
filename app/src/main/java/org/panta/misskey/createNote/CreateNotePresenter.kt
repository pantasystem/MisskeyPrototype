package org.panta.misskey.createNote

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import org.panta.misskey.api.network.Connection
import java.net.URL

class CreateNotePresenter(private val mView: CreateNoteContract.View,private val authKey: String) : CreateNoteContract.Presenter{
    private val mConnection = Connection()

    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    //FIXME 完全にビジネスロジックが混じってしまっているので修正する
    override fun post(text: String) {
        if(text.isEmpty() || text.length > 1500){
            mView.onError("文字数が多すぎる又は少なすぎます")
            return
        }
        val jsonObj = JSONObject()
        jsonObj.apply{
            put("i", authKey)
            put("text", text)
        }

        GlobalScope.launch {
            mConnection.post(URL("https://misskey.xyz/api/notes/create"), jsonObj.toString())

        }
        mView.onPosted()
    }
}