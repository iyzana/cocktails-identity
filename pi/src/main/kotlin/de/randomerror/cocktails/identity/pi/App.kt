package de.randomerror.cocktails.identity.pi

fun main(args: Array<String>) {
    var person: Int? = null

    registerPersonListener { id ->
        person = id
        sendMayDrink(true)
    }

    registerOrderListener { id ->
        person?.let { person ->
            saveOrder(person, id)
        }
    }
}

fun saveOrder(person: Int, order: Int) {

}