package com.owenlejeune.tvtime.utils

import android.content.Context
import android.util.Log
import java.io.BufferedInputStream

object FileUtils {

    private const val TAG = "FileUtils"

    fun getRawResourceFileAsString(context: Context, resourceId: Int): String {
        try {
            val fin = context.resources.openRawResource(resourceId)
            val bis = BufferedInputStream(fin)
            // Note that if a multi-byte character happens to cross this buffer boundary we can end up with a corrupted
            // char in that case. So we make it 4K to cover most, if not all, cases...
            val buffer = ByteArray(4096)
            val sb = java.lang.StringBuilder()
            var bytesRead: Int
            while (bis.read(buffer).also { bytesRead = it } > 0) {
                val text = String(buffer, 0, bytesRead)
                sb.append(text)
            }
            bis.close()
            return sb.toString()
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Could not write file: $resourceId", e)
        }
        return ""
    }

}