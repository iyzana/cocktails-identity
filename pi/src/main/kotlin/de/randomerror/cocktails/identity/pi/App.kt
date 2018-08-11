package de.randomerror.cocktails.identity.pi

fun main(args: Array<String>) {
    var lastPerson: Int? = null

    onPersonScanned { person ->
        lastPerson = person
        sendMayDrink(allOrdersFor(person).size < 3)
    }

    onOrderReceived { order ->
        lastPerson?.let { person ->
            saveOrder(CocktailOrder(person, order))
        }
        lastPerson = null
    }
}

