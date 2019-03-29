package org.panta.misskey.createNote

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_create_note.*
import org.panta.misskey.R

//単純なのでFragmentはない
class CreateNoteActivity : AppCompatActivity() , CreateNoteContract.View{

    override var mPresenter: CreateNoteContract.Presenter = CreateNotePresenter(this, "!Pap6YHn60rmhbwTV81YJKWkMIoX2GKy8")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_note)

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
                mPresenter.post(editText.text.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
