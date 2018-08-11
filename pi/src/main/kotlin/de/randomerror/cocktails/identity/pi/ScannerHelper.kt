package de.randomerror.cocktails.identity.pi

import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

fun onPersonScanned(callback: (Int) -> Unit) {
    thread(isDaemon = false) {
        BufferedReader(InputStreamReader(System.`in`)).lines()
            .map { Integer.parseInt(it) }
            .forEach { callback(it) }
    }
}