package de.randomerror.cocktails.identity.pi

import com.beust.klaxon.Klaxon
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val json = Klaxon()
val logger: Logger = LoggerFactory.getLogger("pi-main")
val syncLock = object {}

fun main(args: Array<String>) {
    dbTransaction { } // initialize db immediately
    FuelManager.instance.basePath = "http://localhost:4567"

    var lastPerson: String? = null

    onPersonScanned { person ->
        logger.info("person scanned: $person")

        if (lastPerson != null)
            return@onPersonScanned

        logger.info("sending may-drink state")

        lastPerson = person
        sendMayDrink(allOrdersFor(person).size < 3)
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
    }
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