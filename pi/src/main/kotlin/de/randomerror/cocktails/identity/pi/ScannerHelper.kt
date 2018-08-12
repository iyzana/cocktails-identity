package de.randomerror.cocktails.identity.pi

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Files.newBufferedReader
import java.nio.file.Paths
import kotlin.concurrent.thread

fun onPersonScanned(callback: (String) -> Unit) {
    thread(isDaemon = false) {
        BufferedReader(InputStreamReader(System.`in`)).lines()
            .forEach { callback(it) }
    }

    // use keyboard file in home directory as alternate input source
    thread(isDaemon = false) {
        if ("nux" in System.getProperty("os.name")) {
            // remove content from keyboard file
            Runtime.getRuntime().exec("sh", arrayOf("-c", "echo -n > keyboard"))
        }

        val filePath = Paths.get(System.getProperty("user.home"), "keyboard")

        newBufferedReader(filePath).use { reader ->
            while (true) {
                val line = reader.readLine() ?: continue

                callback(line)
            }
        }
    }
}