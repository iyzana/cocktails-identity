package de.randomerror.cocktails.identity.backend

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
data class PersonName(
    val code: String,
    val name: String,
    @Id
    @GeneratedValue
    val id: Long = 0
)

fun getNameFor(person: String): PersonName? {
    return dbTransaction {
        createQuery("select p from PersonName p where p.code = :code", PersonName::class.java)
            .setParameter("code", person)
            .uniqueResult()
    }
}

fun setNameFor(person: String, name: String) {
    val oldEntity = getNameFor(person)
    val entity = oldEntity?.copy(name = name) ?: PersonName(person, name)

    oldEntity?.let { dbTransaction { evict(it) } } // let hibernate think the copy source does not exist
    dbTransaction { saveOrUpdate(entity) }
}