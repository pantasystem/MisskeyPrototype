package org.panta.misskey.obj

sealed class Content{
    data class Note(var noteId: String, var date: String, var text: String, var user: UserData): Content()
    data class ReNote(var reNoteUser: UserData, var date: String, var note: Note): Content()
}