package pacman3d

import kotlinx.browser.window
import pacman3d.Sound.*
import three.js.*

fun Camera.add(soundPlayer: SoundPlayer) {
    add(soundPlayer.audioListener)
}

enum class Sound(val filename: String) {
    Intro("sounds/intro.mp3"),
    Chomp("sounds/chomp.mp3"),
    LifeLost("sounds/life-lost.mp3"),
    EatGhost("sounds/eat-ghost.mp3"),
}

object SoundPlayer {

    val audioListener = AudioListener()

    private val audioLoader = AudioLoader()

    private val sounds = arrayOf(
        newSound(Intro),
        newSound(Chomp).apply { offset = 0.139 },
        newSound(LifeLost),
        newSound(EatGhost),
    )

    init {
        window.addEventListener("keydown", {
            audioListener.context.asDynamic().resume() as Unit
        })
    }

    private fun newSound(sound: Sound) = Audio<AudioNode>(audioListener).apply {
        setVolume(0.5)
        audioLoader.load(sound.filename, onLoad = { buffer -> setBuffer(buffer) })
    }

    fun play(sound: Sound): Unit = sounds[sound.ordinal].run {
        buffer?.also {
            if (!isPlaying) play()
        }
    }
}