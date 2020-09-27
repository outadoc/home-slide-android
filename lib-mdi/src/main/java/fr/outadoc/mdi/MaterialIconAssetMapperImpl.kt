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
