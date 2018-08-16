package de.randomerror.cocktails.identity.backend

import com.beust.klaxon.Converter
import com.beust.klaxon.JsonValue
import com.beust.klaxon.Klaxon
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Request
import spark.Response
import spark.Spark
import spark.kotlin.after
import spark.kotlin.get
import spark.kotlin.halt
import spark.kotlin.internalServerError
import spark.kotlin.notFound
import spark.kotlin.port
import spark.kotlin.post
import java.time.LocalDateTime

val json = Klaxon()
    .converter(object: Converter {
        override fun canConvert(cls: Class<*>) = cls == LocalDateTime::class.java

        override fun fromJson(jv: JsonValue) = LocalDateTime.parse(jv.string)

        override fun toJson(value: Any) = """"${(value as LocalDateTime)}""""
    })

val logger: Logger = LoggerFactory.getLogger("backend-main")

fun main(args: Array<String>) {
    dbTransaction { } // initialize db immediately

    port(28472)

    after {
        response.type("application/json")
    }

    post("/saveOrders", accepts = "application/json") {
        val orders = json.parseArray<CocktailOrder>(request.body()) ?: throw halt(400)
        saveAllOrders(orders)

        logger.info("saved ${orders.size} orders")

        json.toJsonString("ok")
    }

    get("/orders/:person") {
        val person = request.params("person")

        logger.info("loading orders for $person")

        json.toJsonString(allOrdersFor(person))
    }

//    get("/name/:person") {
//        val person = request.params("person")
//
//        getNameFor(person)?.name ?: throw halt(404)
//    }
//
//    post("/name/:person/:name") {
//        val person = request.params("person")
//        val name = request.params("name")
//
//        setNameFor(person, name)
//        "ok"
//    }

    handleErrors()
}

fun handleErrors() {
    notFound {
        json.toJsonString(
            Error(
                "not found",
                "the route ${request.requestMethod()} ${request.pathInfo()} does not exist"
            )
        )
    }

    internalServerError {
        json.toJsonString(
            Error(
                "internal error",
                "unknown cause"
            )
        )
    }

    exception<NumberFormatException> { ex, _, _ ->
        json.toJsonString(
            Error(
                "invalid number format",
                "invalid number format ${ex.localizedMessage}"
            )
        )
    }
}

inline fun <reified T : Exception> exception(noinline handler: (T, Request, Response) -> String) {
    Spark.exception(T::class.java) { ex, req, res ->
        res.status(400)
        res.type("application/json")
        val responseBody = handler(ex, req, res)
        if (res.body() == null)
            res.body(responseBody)
    }
}

inline fun <reified T : Exception> exception() {
    exception<T> { ex, _, _ ->
        """
            {
                "error": ${json.toJsonString(ex.javaClass.simpleName)},
                "message": ${json.toJsonString(ex.localizedMessage)}
            }
        """.trimIndent()
    }
}
