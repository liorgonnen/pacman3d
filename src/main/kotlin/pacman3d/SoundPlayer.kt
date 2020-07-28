package pacman3d

import kotlinx.browser.window
import three.js.*

fun Camera.add(soundPlayer: SoundPlayer) {
    add(soundPlayer.audioListener)
}

enum class Sound(val filename: String) {
    Chomp("sounds/chomp.mp3"),
}

object SoundPlayer {

    val audioListener = AudioListener()

    private val audioLoader = AudioLoader()

    private val sounds = arrayOf(
        newSound(Sound.Chomp).apply { offset = 0.139 }
    )

    init {
        window.addEventListener("keydown", {
            audioListener.context.asDynamic().resume()
        })
    }

    private fun newSound(sound: Sound) = Audio<AudioNode>(audioListener).apply {
        setVolume(0.5)
        audioLoader.load(sound.filename, onLoad = { buffer -> setBuffer(buffer) })
    }

    fun play(sound: Sound): Unit = sounds[sound.ordinal].run {
//        buffer?.also {
//            if (!isPlaying) play()
//        }
    }
}