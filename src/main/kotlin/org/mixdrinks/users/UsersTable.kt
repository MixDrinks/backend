package org.mixdrinks.users

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object UsersTable : IdTable<String>(name = "users") {

    override val id: Column<EntityID<String>> = text("user_id").entityId()

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

data class User(val userId: EntityID<String>) : Entity<String>(userId) {
    companion object : EntityClass<String, User>(UsersTable)
}
