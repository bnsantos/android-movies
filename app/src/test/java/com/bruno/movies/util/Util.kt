package com.bruno.movies.util

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import java.io.InputStreamReader
import java.lang.reflect.Type

fun <T> loadFromResource(fileName: String, loader: ClassLoader, type: Type): T {
    val resourceAsStream = resourceAsInputStream(loader, fileName)
    val targetReader = InputStreamReader(resourceAsStream)
    val reader = JsonReader(targetReader)
    val data = Gson().fromJson<T>(reader, type)
    targetReader.close()
    return data
}

private fun resourceAsInputStream(loader: ClassLoader, fileName: String) = loader.getResourceAsStream("api-response/" + fileName)
