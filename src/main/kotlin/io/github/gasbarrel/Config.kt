package io.github.gasbarrel

import com.akuleshov7.ktoml.Toml
import com.freya02.botcommands.api.core.annotations.BService
import com.freya02.botcommands.api.core.suppliers.annotations.InstanceSupplier
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import mu.KotlinLogging
import kotlin.io.path.Path
import kotlin.io.path.readText

val config: Config by lazy {
    Config.logger.info("Loading configuration at ${Environment.configFilePath}")

    return@lazy Toml.decodeFromString(Environment.configFilePath.readText())
}

@Serializable
data class DatabaseConfig(
    @SerialName("server-name")
    val serverName: String,
    val port: Int,
    val name: String,
    val user: String,
    val password: String
) {
    val url: String
        get() = "jdbc:postgresql://$serverName:$port/$name"
}

@BService
@Serializable
data class Config(
    val token: String,
    val prefixes: List<String>,
    @SerialName("owner-ids")
    val ownerIds: List<Long>,
    @SerialName("test-guild-ids")
    val testGuildIds: List<Long>,
    val database: DatabaseConfig
) {
    companion object {
        internal val logger = KotlinLogging.logger {}

        val folder = Path(".", if (Environment.isDev) "dev-config" else "config")

        @InstanceSupplier
        fun supply(): Config = config
    }
}
