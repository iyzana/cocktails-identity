package de.randomerror.cocktails.identity.pi

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

fun saveOrder(order: CocktailOrder) {
    return dbTransaction { save(order) }
}

fun allOrdersFor(person: String): List<CocktailOrder> {
    return dbTransaction {
        createQuery("select o from CocktailOrder o where o.person = :person", CocktailOrder::class.java)
            .setParameter("person", person)
            .list()
    }
}