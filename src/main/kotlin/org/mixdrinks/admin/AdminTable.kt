package org.mixdrinks.admin

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object AdminTable : IntIdTable(name = "admins", columnName = "id") {
    val name = text("login").uniqueIndex()
    val password = binary("password")
}

class Admin(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Admin>(AdminTable)

    var login by AdminTable.name
    var password by AdminTable.password
}
