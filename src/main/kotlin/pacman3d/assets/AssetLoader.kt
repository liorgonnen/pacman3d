package pacman3d.assets

import three.js.Font
import three.js.FontLoader

private typealias Callback = (Font) -> Unit

object AssetLoader {

    private const val FONT = "fonts/emulogic.json"

    private lateinit var font: Font

    private val fontLoadedCallbacks = ArrayList<Callback>()

    init {
        FontLoader().load(FONT, onLoad = ::executeFontLoadedCallbacks)
    }

    private fun executeFontLoadedCallbacks(font: Font) {
        fontLoadedCallbacks.forEach { it.invoke(font) }
        fontLoadedCallbacks.clear()
    }

    fun onFontLoaded(callback: Callback) {
        if (::font.isInitialized) callback(font) else fontLoadedCallbacks += callback
    }
}