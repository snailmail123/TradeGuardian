package com.penguinstudios.tradeguardian.util

import android.content.Context
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.charset.StandardCharsets

object AssetUtil {

    //Loads contract binary from txt file
    @Throws(IOException::class)
    fun getContractBin(context: Context, fileName: String): String {
        return try {
            context.assets.open(fileName).use { inputStream ->
                inputStream.bufferedReader(StandardCharsets.UTF_8).use { reader ->
                    reader.readText()
                }
            }
        } catch (e: FileNotFoundException) {
            throw RuntimeException("No txt file in assets folder found for: $fileName", e)
        } catch (e: IOException) {
            throw IOException("Buffered reader failed to read.", e)
        }
    }
}
