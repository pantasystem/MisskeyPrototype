package org.panta.misskey.obj

//NoteならNoteとNoteである情報、Re NoteならRe Noteである情報とRe Noteの情報
data class ContentData(var contentType: ContentTypeEnum,var id: String, var content: Content)

enum class ContentTypeEnum{
    NOTE,
    RE_NOTE
}