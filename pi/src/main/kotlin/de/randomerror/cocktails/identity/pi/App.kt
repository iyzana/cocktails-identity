package de.randomerror.cocktails.identity.pi

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import de.randomerror.cocktails.identity.pi.DrinkAllowance.ALL
import de.randomerror.cocktails.identity.pi.DrinkAllowance.LEFT1
import de.randomerror.cocktails.identity.pi.DrinkAllowance.LEFT2
import de.randomerror.cocktails.identity.pi.DrinkAllowance.LEFT3
import de.randomerror.cocktails.identity.pi.DrinkAllowance.NONE
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val json = Klaxon()
val logger: Logger = LoggerFactory.getLogger("pi-main")
val syncLock = object {}

fun main(args: Array<String>) {
    dbTransaction { } // initialize db immediately
    FuelManager.instance.basePath = "http://cocktails.randomerror.de:28472/"

    var lastPerson: String? = null

    onPersonScanned { person ->
        logger.info("person scanned: $person")

        if (lastPerson != null)
            return@onPersonScanned

        lastPerson = person

        val allowance = computeDrinkAllowance(person) ?: return@onPersonScanned

        logger.info("sending may-drink state")
        sendMayDrink(allowance)
    }

    onOrderReceived { order ->
        logger.info("order received: $order")

        val person = lastPerson ?: return@onOrderReceived

        logger.info("saving order")

        saveOrder(CocktailOrder(person, order))
        syncOrderDbs()
    }

    onPersonDone {
        logger.info("person done")

        lastPerson = null
        sendKeepAlive()
    }
}

fun computeDrinkAllowance(person: String): DrinkAllowance? {
    val number = person.toIntOrNull() ?: return null
    
    return when (number) {
        in 0..99 -> ALL
        in 100..199 -> NONE
        in 200..499 -> ALL
        in 500..599 -> remainingTeenDrinkAllowance(person)
        else -> null
    }
}

fun remainingTeenDrinkAllowance(person: String) =
    when (allOrdersFor(person).size) {
        0 -> LEFT3
        1 -> LEFT2
        2 -> LEFT1
        else -> NONE
    }

fun syncOrderDbs() = synchronized(syncLock) {
    logger.info("trying to sync dbs")
    val unsyncedOrders = allUnsynced()

    val (_, _, result) = "/saveOrders".httpPost()
        .timeout(2000)
        .timeoutRead(2000)
        .body(json.toJsonString(unsyncedOrders))
        .response()

    when (result) {
        is Result.Success -> {
            unsyncedOrders
                .onEach { dbTransaction { evict(it) } }
                .map { it.copy(synced = true) }
                .also { saveAllOrders(it) }
            logger.info("db sync successful")
        }
        is Result.Failure -> {
            logger.info("db sync failed")
        }
    }
}
