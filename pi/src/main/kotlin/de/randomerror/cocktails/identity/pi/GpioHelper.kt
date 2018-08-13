package de.randomerror.cocktails.identity.pi

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.PinEdge.RISING
import com.pi4j.io.gpio.PinPullResistance.PULL_DOWN
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.PinState.HIGH
import com.pi4j.io.gpio.PinState.LOW
import com.pi4j.io.gpio.RaspiPin.GPIO_00
import com.pi4j.io.gpio.RaspiPin.GPIO_01
import com.pi4j.io.gpio.RaspiPin.GPIO_02
import com.pi4j.io.gpio.RaspiPin.GPIO_03
import com.pi4j.io.gpio.RaspiPin.GPIO_04
import com.pi4j.io.gpio.RaspiPin.GPIO_05
import com.pi4j.io.gpio.RaspiPin.GPIO_06
import com.pi4j.io.gpio.RaspiPin.GPIO_07
import com.pi4j.io.gpio.RaspiPin.GPIO_08
import com.pi4j.io.gpio.event.GpioPinListenerDigital
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


val controller = GpioFactory.getInstance()

val mayDrinkPulsePin = controller.provisionDigitalOutputPin(GPIO_00, LOW)
val mayDrinkValuePin = controller.provisionDigitalOutputPin(GPIO_01, LOW)

val orderedPulsePin = controller.provisionDigitalInputPin(GPIO_02, PULL_DOWN)
val orderedValuePin1 = controller.provisionDigitalInputPin(GPIO_03, PULL_DOWN)
val orderedValuePin2 = controller.provisionDigitalInputPin(GPIO_04, PULL_DOWN)
val orderedValuePin3 = controller.provisionDigitalInputPin(GPIO_05, PULL_DOWN)
val orderedValuePin4 = controller.provisionDigitalInputPin(GPIO_06, PULL_DOWN)

val resetPulsePin = controller.provisionDigitalInputPin(GPIO_07, PULL_DOWN)
val resetAckPin = controller.provisionDigitalOutputPin(GPIO_08, LOW)

fun sendMayDrink(mayDrink: Boolean) {
    mayDrinkValuePin.setState(mayDrink)
    mayDrinkPulsePin.pulse(15)

    // expect a rising edge within one second, else throw exception
    Executors.newSingleThreadScheduledExecutor().submit {
        val a = CountDownLatch(1)

        // todo: create pin for this and change it
        resetPulsePin.addListener(GpioPinListenerDigital { event ->
            if (event.edge == RISING)
                a.countDown()
        })

        a.await()
    }.get(1, TimeUnit.SECONDS)
}

fun onOrderReceived(callback: (Int) -> Unit) {
    orderedPulsePin.addListener(GpioPinListenerDigital { event ->
        if (event.edge == RISING) {
            val id = toInt(
                orderedValuePin1.state,
                orderedValuePin2.state,
                orderedValuePin3.state,
                orderedValuePin4.state
            )

            callback(id)
        }
    })
}

fun onPersonDone(callback: () -> Unit) {
    resetPulsePin.addListener(GpioPinListenerDigital { event ->
        if (event.edge == RISING) {
            callback()
            resetAckPin.pulse(15)
        }
    })
}

private fun toInt(vararg states: PinState): Int {
    return states.map { toInt(it) }
        .map { it.toString() }
        .joinToString("")
        .let { Integer.parseInt(it, 2) }
}

private fun toInt(state: PinState): Int {
    return if (state == HIGH) 1 else 0
}