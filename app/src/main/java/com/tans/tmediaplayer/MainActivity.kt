package com.tans.tmediaplayer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.TextureView
import android.widget.TextView
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val ioExecutor: Executor by lazy {
    Executors.newFixedThreadPool(2)
}

class MainActivity : AppCompatActivity() {

    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }
    private val textureView by lazy {
        findViewById<TextureView>(R.id.texture_view)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val file = "gokuraku.mp4"
        ioExecutor.execute {
            val parentDir = filesDir
            val testVideoFile = File(parentDir, file)
            if (!testVideoFile.exists()) {
                testVideoFile.createNewFile()
                FileOutputStream(testVideoFile).buffered(1024).use { output ->
                    val buffer = ByteArray(1024)
                    assets.open(file).buffered(1024).use { input ->
                        var thisTimeRead: Int = 0
                        do {
                            thisTimeRead = input.read(buffer)
                            if (thisTimeRead > 0) {
                                output.write(buffer, 0, thisTimeRead)
                            }
                        } while (thisTimeRead > 0)
                    }
                    output.flush()
                }
            }
            mediaPlayer.setupPlayer(testVideoFile.absolutePath, textureView)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.releasePlayer()
    }
}