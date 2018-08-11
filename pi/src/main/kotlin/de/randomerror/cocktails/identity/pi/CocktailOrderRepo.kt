package de.randomerror.cocktails.identity.pi

fun saveOrder(order: CocktailOrder) {
    return dbTransaction { save(order) }
}

fun allOrdersFor(person: Int): List<CocktailOrder> {
    return dbTransaction {
        createQuery(
            "select o from CocktailOrder o where o.person = :person",
            CocktailOrder::class.java
        ).setParameter("person", person).list()
    }
}