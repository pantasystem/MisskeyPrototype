package org.panta.misskey.obj

import android.graphics.Bitmap
import kotlinx.coroutines.Deferred
import java.io.InputStream

data class UserData(var name: String, var userId: String, var profileImage: Deferred<Bitmap?>, var isCat:String, var isBot: String, var primaryId: String)