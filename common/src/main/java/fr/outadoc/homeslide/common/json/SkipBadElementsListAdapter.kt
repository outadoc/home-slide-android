package fr.outadoc.homeslide.common.json

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import fr.outadoc.homeslide.logging.KLog
import java.lang.reflect.Type

class SkipBadElementsListAdapter<T>(private val elementAdapter: JsonAdapter<T?>) :
    JsonAdapter<List<T?>>() {

    companion object {
        fun newFactory(): Factory {
            return object : Factory {
                override fun create(
                    type: Type,
                    annotations: Set<Annotation>,
                    moshi: Moshi
                ): JsonAdapter<*>? {
                    if (Types.getRawType(type) != List::class.java) {
                        return null
                    }

                    val elementType = Types.collectionElementType(type, List::class.java)
                    val elementAdapter = moshi.adapter<Any?>(elementType)
                    return SkipBadElementsListAdapter(elementAdapter)
                }
            }
        }
    }

    override fun fromJson(reader: JsonReader): List<T?>? {
        val result = mutableListOf<T?>()
        reader.beginArray()

        while (reader.hasNext()) {
            try {
                val peeked = reader.peekJson()
                result += elementAdapter.fromJson(peeked)
            } catch (e: JsonDataException) {
                KLog.e(e) { "Couldn't parse item. Skipping." }
            }
            reader.skipValue()
        }

        reader.endArray()
        return result
    }

    override fun toJson(writer: JsonWriter, value: List<T?>?) {
        if (value == null) {
            throw NullPointerException("value was null! Wrap in .nullSafe() to write nullable values.")
        }

        writer.beginArray()

        for (i in value.indices) {
            elementAdapter.toJson(writer, value[i])
        }

        writer.endArray()
    }
}