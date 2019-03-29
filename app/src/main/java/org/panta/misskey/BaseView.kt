package org.panta.misskey

interface BaseView<T: BasePresenter> {
    var mPresenter: T
}