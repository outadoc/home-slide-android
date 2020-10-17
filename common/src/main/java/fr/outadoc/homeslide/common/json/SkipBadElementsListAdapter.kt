/*
 * Copyright 2020 Baptiste Candellier
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
