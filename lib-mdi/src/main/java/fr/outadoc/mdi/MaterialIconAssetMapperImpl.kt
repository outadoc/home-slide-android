package fr.outadoc.mdi

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * Maps an icon name to a unicode codepoint that can be displayed using the MDI font.
 */
class MaterialIconAssetMapperImpl(private val context: Context) : MaterialIconMapper {

    companion object {
        const val FILENAME = "mdi_map.txt"
        const val SEPARATOR = ' '
        const val COMMENT = '#'
    }

    override fun getIcon(@IconStringRef iconName: String): FontIcon? {
        val icon = iconMap[iconName]
        return icon?.let { FontIcon(it) }
    }

    private fun loadMap(reader: BufferedReader): Map<String, String> {
        val map = mutableMapOf<String, String>()

        reader.forEachLine { line ->
            if (line.startsWith(COMMENT) || line.isBlank())
                return@forEachLine

            val id = line.takeWhile { it != SEPARATOR }
            val char = line.takeLastWhile { it != SEPARATOR }
            map[id] = char
        }

        return map
    }

    private val iconMap: Map<String, String> by lazy {
        val file = context.assets.open(FILENAME)
        BufferedReader(InputStreamReader(file, Charset.forName("utf-8"))).use { reader ->
            loadMap(reader)
        }
    }
}
