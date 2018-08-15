package de.randomerror.cocktails.identity.backend

import com.beust.klaxon.Klaxon
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

val json = Klaxon()

fun main(args: Array<String>) {
    dbTransaction { } // initialize db immediately

    port(28472)

    after {
        response.type("application/json")
    }

    post("/saveOrders", accepts = "application/json") {
        val orders = json.parseArray<CocktailOrder>(request.body()) ?: throw halt(400)
        saveAllOrders(orders)

        json.toJsonString("ok")
    }

    get("/orders/:person") {
        val person = request.params("person")

        json.toJsonString(allOrdersFor(person))
    }

    get("/name/:person") {
        val person = request.params("person")

        getNameFor(person)?.name ?: throw halt(404)
    }

    post("/name/:person/:name") {
        val person = request.params("person")
        val name = request.params("name")

        setNameFor(person, name)
        "ok"
    }

    handleErrors()
}

fun handleErrors() {
    notFound {
        """
            {
                "error": "not found",
                "message": "the route ${request.requestMethod()} ${request.pathInfo()} does not exist"
            }
        """.trimIndent()
    }

    internalServerError {
        """
            {
                "error": "internal error",
                "message": "unknown error"
            }
        """.trimIndent()
    }

//    exception<NumberFormatException>()
    exception<NumberFormatException> { ex, _, _ ->
        """
            {
                "error": "invalid number format",
                "message": ${
        json.toJsonString("invalid number format ${ex.localizedMessage}")
        }
            }
        """.trimIndent()
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
