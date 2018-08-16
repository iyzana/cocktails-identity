package de.randomerror.cocktails.identity.pi

import java.time.LocalDateTime
import java.time.LocalDateTime.now
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class CocktailOrder(
    val person: String,
    val cocktail: Int,
    val orderTime: LocalDateTime = now(),
    val synced: Boolean = false,
    @Id
    @GeneratedValue
    val id: Long = 0
)

fun saveOrder(order: CocktailOrder) {
    return dbTransaction { save(order) }
}

fun saveAllOrders(orders: List<CocktailOrder>) {
    dbTransaction {
        orders.forEach { save(it) }
    }
}

fun allOrdersFor(person: String): List<CocktailOrder> {
    return dbTransaction {
        createQuery("select o from CocktailOrder o where o.person = :person", CocktailOrder::class.java)
            .setParameter("person", person)
            .list()
    }
}

fun allUnsynced(): List<CocktailOrder> {
    return dbTransaction {
        createQuery("select o from CocktailOrder o where o.synced = false", CocktailOrder::class.java)
            .list()
    }
}