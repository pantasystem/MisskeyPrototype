package org.panta.misskey.createNote

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_create_note.*
import org.panta.misskey.R

//単純なのでFragmentはない
class CreateNoteActivity : AppCompatActivity() , CreateNoteContract.View{

    companion object {
        const val TARGET_NOTE_ID = "TARGET_NOTE_ID"
        const val NOTE_TYPE = "NOTE_TYPE"
        const val NOTE_TYPE_NORMAL = 0
        const val NOTE_TYPE_RENOTE = 1
        const val NOTE_TYPE_REPLY = 2
    }

    override var mPresenter: CreateNoteContract.Presenter = CreateNotePresenter(this, "!Pap6YHn60rmhbwTV81YJKWkMIoX2GKy8")

    private var mNoteType = 0
    private var mTargetNoteId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

        val intent = intent
        mNoteType = intent.getIntExtra(NOTE_TYPE, 0)
        mTargetNoteId = intent.getStringExtra(TARGET_NOTE_ID)
        when(mNoteType){
            NOTE_TYPE_NORMAL ->{

            }
            NOTE_TYPE_RENOTE ->{

            }
            NOTE_TYPE_REPLY ->{

            }
        }

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

    }



    override fun onError(msg: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPosted() {
        finish()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu_create_note_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home -> finish()
            R.id.postNote ->{
                val text = editText.text.toString()
                when(mNoteType){
                    NOTE_TYPE_NORMAL ->{
                        mPresenter.normalPost(text)
                    }

                }
                mTargetNoteId?:return super.onOptionsItemSelected(item)
                when(mNoteType){
                    NOTE_TYPE_RENOTE ->{
                        mPresenter.reNote(mTargetNoteId!!, text)
                    }
                    NOTE_TYPE_REPLY ->{
                        mPresenter.reply(mTargetNoteId!!, text)
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
