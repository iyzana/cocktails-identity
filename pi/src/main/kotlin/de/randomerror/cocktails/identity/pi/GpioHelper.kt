package de.randomerror.cocktails.identity.pi

import com.pi4j.io.gpio.GpioFactory
import com.pi4j.io.gpio.PinEdge.RISING
import com.pi4j.io.gpio.PinPullResistance.PULL_DOWN
import com.pi4j.io.gpio.PinState
import com.pi4j.io.gpio.PinState.HIGH
import com.pi4j.io.gpio.PinState.LOW
import com.pi4j.io.gpio.RaspiPin.GPIO_00
import com.pi4j.io.gpio.RaspiPin.GPIO_02
import com.pi4j.io.gpio.RaspiPin.GPIO_03
import com.pi4j.io.gpio.RaspiPin.GPIO_04
import com.pi4j.io.gpio.RaspiPin.GPIO_07
import com.pi4j.io.gpio.RaspiPin.GPIO_08
import com.pi4j.io.gpio.RaspiPin.GPIO_09
import com.pi4j.io.gpio.RaspiPin.GPIO_12
import com.pi4j.io.gpio.RaspiPin.GPIO_13
import com.pi4j.io.gpio.RaspiPin.GPIO_14
import com.pi4j.io.gpio.RaspiPin.GPIO_15
import com.pi4j.io.gpio.event.GpioPinListenerDigital


val lock = object {}
val controller = GpioFactory.getInstance()!!

val mayDrinkPulsePin = controller.provisionDigitalOutputPin(GPIO_15, LOW)!!
val mayDrinkValue1Pin = controller.provisionDigitalOutputPin(GPIO_08, LOW)!!
val mayDrinkValue2Pin = controller.provisionDigitalOutputPin(GPIO_09, LOW)!!
val mayDrinkValue3Pin = controller.provisionDigitalOutputPin(GPIO_07, LOW)!!

val orderedPulsePin = controller.provisionDigitalInputPin(GPIO_00, PULL_DOWN)!!
val orderedValuePin1 = controller.provisionDigitalInputPin(GPIO_02, PULL_DOWN)!!
val orderedValuePin2 = controller.provisionDigitalInputPin(GPIO_03, PULL_DOWN)!!
val orderedValuePin3 = controller.provisionDigitalInputPin(GPIO_12, PULL_DOWN)!!
val orderedValuePin4 = controller.provisionDigitalInputPin(GPIO_13, PULL_DOWN)!!
val orderReceivedPin = controller.provisionDigitalOutputPin(GPIO_14, LOW)!!

val resetPulsePin = controller.provisionDigitalInputPin(GPIO_04, PULL_DOWN)!!

fun sendMayDrink(allowance: DrinkAllowance) = synchronized(lock) {
    val pinStates = toStateArray(allowance.ordinal + 1, 3)
    mayDrinkValue1Pin.state = pinStates[2]
    mayDrinkValue2Pin.state = pinStates[1]
    mayDrinkValue3Pin.state = pinStates[0]
    mayDrinkPulsePin.pulse(15, true)
    mayDrinkValue1Pin.state = LOW
    mayDrinkValue2Pin.state = LOW
    mayDrinkValue3Pin.state = LOW
}

fun onOrderReceived(callback: (Int) -> Unit) {
    orderedPulsePin.addListener(GpioPinListenerDigital { event ->
        if (event.edge == RISING) {
            val id = toInt(
                orderedValuePin4.state,
                orderedValuePin3.state,
                orderedValuePin2.state,
                orderedValuePin1.state
            ) + 1

            callback(id)
            orderReceivedPin.pulse(15)
        }
    })
}

fun onPersonDone(callback: () -> Unit) {
    resetPulsePin.addListener(GpioPinListenerDigital { event ->
        if (event.edge == RISING) {
            callback()
        }
    })
}

fun sendKeepAlive(): Unit = synchronized(lock) {
    mayDrinkValue1Pin.pulse(15)
    mayDrinkValue2Pin.pulse(15)
    mayDrinkValue3Pin.pulse(15)
    orderReceivedPin.pulse(15)
}

private fun toStateArray(value: Int, size: Int): List<PinState> {
    return Integer.toBinaryString(value)
        .trimStart('0')
        .padStart(size, '0')
        .toCharArray()
        .map { if (it == '1') HIGH else LOW }
        .toList()
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