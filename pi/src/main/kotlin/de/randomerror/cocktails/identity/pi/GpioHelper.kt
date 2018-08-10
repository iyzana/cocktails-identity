package de.randomerror.cocktails.identity.pi

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.PinEdge.RISING
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.PinState.HIGH
import com.pi4j.io.gpio.PinState.LOW
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.io.gpio.event.GpioPinListenerDigital


val controller = GpioFactory.getInstance()

val mayDrinkPulsePin = controller.provisionDigitalOutputPin(RaspiPin.GPIO_00, LOW)
val mayDrinkValuePin = controller.provisionDigitalOutputPin(RaspiPin.GPIO_01, LOW)

val orderedPulsePin = controller.provisionDigitalInputPin(RaspiPin.GPIO_02)
val orderedValuePin1 = controller.provisionDigitalInputPin(RaspiPin.GPIO_03)
val orderedValuePin2 = controller.provisionDigitalInputPin(RaspiPin.GPIO_04)
val orderedValuePin3 = controller.provisionDigitalInputPin(RaspiPin.GPIO_05)
val orderedValuePin4 = controller.provisionDigitalInputPin(RaspiPin.GPIO_06)

fun sendMayDrink(mayDrink: Boolean) {
    mayDrinkValuePin.setState(mayDrink)
    mayDrinkPulsePin.pulse(20)
}

fun registerOrderListener(callback: (Int) -> Unit) {
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

private fun toInt(vararg states: PinState): Int {
    return states.map { toInt(it) }
        .map { it.toString() }
        .joinToString("")
        .let { Integer.parseInt(it, 2) }
}

private fun toInt(state: PinState): Int {
    return if (state == HIGH) 1 else 0
}