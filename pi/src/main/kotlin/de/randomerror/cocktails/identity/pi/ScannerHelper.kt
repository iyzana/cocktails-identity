package de.randomerror.cocktails.identity.pi

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

fun onPersonScanned(callback: (String) -> Unit) {
    thread(isDaemon = false) {
        BufferedReader(InputStreamReader(System.`in`)).lines()
            .forEach { callback(it) }
    }
}