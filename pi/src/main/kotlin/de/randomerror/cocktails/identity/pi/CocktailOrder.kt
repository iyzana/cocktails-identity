package de.randomerror.cocktails.identity.pi

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class CocktailOrder(
    val person: Int,
    val cocktail: Int,
    @Id
    @GeneratedValue
    val id: Long = 0
)