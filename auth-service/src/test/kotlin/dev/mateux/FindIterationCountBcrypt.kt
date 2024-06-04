package dev.mateux

import io.quarkus.elytron.security.common.BcryptUtil
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

@Disabled
class FindIterationCountBcrypt {
    @Test
    fun findIterationCount() {
        val password = "password"
        val costs = (4..16).toList()
        val expectedTimeMillis = 1000L
        val times = costs.map {
            measureTimeMillis {
                BcryptUtil.bcryptHash(password, it)
            }
        }

        println("optimal cost: ${costs[times.indexOfFirst { it > expectedTimeMillis }]}")
    }
}