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

package fr.outadoc.mdi

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.TreeMap

/**
 * Maps an icon name to a unicode codepoint that can be displayed using the MDI font.
 */
class MaterialIconAssetMapperImpl(context: Context) : MaterialIconMapper {

    companion object {
        private const val FILENAME = "mdi_map.txt"
        private const val SEPARATOR = ' '
    }

    private val iconMap: Map<String, String>

    init {
        context.assets.open(FILENAME).use { file ->
            iconMap = BufferedReader(
                InputStreamReader(file, StandardCharsets.UTF_8)
            ).use { reader ->
                loadMap(reader)
            }
        }
    }

    override fun getIcon(@IconStringRef iconName: String): FontIcon? {
        return iconMap[iconName]?.let { FontIcon(it) }
    }

    private fun loadMap(reader: BufferedReader): Map<String, String> {
        val map = TreeMap<String, String>()
        reader.forEachLine { line ->
            val split = line.split(SEPARATOR, limit = 2)
            map[split[0]] = split[1]
        }
        return map
    }
}
