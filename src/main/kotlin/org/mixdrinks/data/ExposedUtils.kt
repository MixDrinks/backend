package org.mixdrinks.data

import org.jetbrains.exposed.sql.BooleanColumnType
import org.jetbrains.exposed.sql.CustomFunction
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.QueryBuilder

/**
 * SELECT DISTINCT ON (a,b,c) TRUE, a,b,x,y,z FROM table WHERE ...
 * see: https://github.com/JetBrains/Exposed/issues/500
 */
fun distinctOn(vararg expressions: Expression<*>): CustomFunction<Boolean?> = CustomBooleanFunction(
    functionName = "DISTINCT ON",
    postfix = " TRUE",
    params = expressions
)

@Suppress("FunctionName")
fun CustomBooleanFunction(
    functionName: String, postfix: String = "", vararg params: Expression<*>
): CustomFunction<Boolean?> =
    object : CustomFunction<Boolean?>(functionName, BooleanColumnType(), *params) {
        override fun toQueryBuilder(queryBuilder: QueryBuilder) {
            super.toQueryBuilder(queryBuilder)
            if (postfix.isNotEmpty()) {
                queryBuilder.append(postfix)
            }
        }
    }


fun Query.toSQL():String = prepareSQL(QueryBuilder(false))
