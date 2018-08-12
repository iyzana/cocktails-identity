package de.randomerror.cocktails.identity.backend

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class CocktailOrder(
    val person: String,
    val cocktail: Int,
    @Id
    @GeneratedValue
    val id: Long = 0
)

fun saveAllOrders(orders: List<CocktailOrder>) {
    return dbTransaction {
        orders.forEach { save(it)}
    }
}

fun allOrdersFor(person: String): List<CocktailOrder> {
    return dbTransaction {
        createQuery(
            "select o from CocktailOrder o where o.person = :person",
            CocktailOrder::class.java
        ).setParameter("person", person).list()
    }
}