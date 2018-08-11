package de.randomerror.cocktails.identity.pi

import org.hibernate.Session
import org.hibernate.cfg.Configuration

private val db: Session = Configuration()
    .setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
    .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
    .setProperty("hibernate.connection.url", "jdbc:h2:./cocktails-identity")
    .setProperty("hibernate.hbm2ddl.auto", "update")
    .addAnnotatedClass(CocktailOrder::class.java)
    .buildSessionFactory()
    .openSession()

fun <T> dbTransaction(function: Session.() -> T): T {
    val tx = db.beginTransaction()
    try {
        val result = function(db)
        tx.commit()
        return result
    } catch (e: Exception) {
        tx.rollback()
        throw e
    }
}


