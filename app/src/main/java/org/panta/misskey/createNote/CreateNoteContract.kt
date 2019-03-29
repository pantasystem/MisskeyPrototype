package org.panta.misskey.createNote

import org.panta.misskey.BasePresenter
import org.panta.misskey.BaseView

interface CreateNoteContract{
    interface View : BaseView<Presenter> {

        fun onPosted()
        fun onError(msg: String)

    }

    interface Presenter : BasePresenter{

        fun post(text: String)
    }
}