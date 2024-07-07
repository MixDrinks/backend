package org.mixdrinks.mongo

import com.mongodb.client.model.Filters
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Mongo(connectionString: String) {

    private val client by lazy {
        MongoClient.create(connectionString = connectionString)
    }

    private val database by lazy {
        client.getDatabase("mixdrinks")
    }

    init {
        GlobalScope.launch {
            database.listCollections().collect {
                println(it)
            }
        }
    }

    data class MongoCocktail(
        val id: Int,
        val visitCount: Int,
    )

    suspend fun incVisitCount(id: Int) {
        val queryParam = Filters.eq("id", id)
        val updateParams = Updates.inc("visitCount", 1)
        database.getCollection<MongoCocktail>(collectionName = "cocktails")
            .updateOne(
                filter = queryParam,
                update = updateParams,
            )
    }
}
