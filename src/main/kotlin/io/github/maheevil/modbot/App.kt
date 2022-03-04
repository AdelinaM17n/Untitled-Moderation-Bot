package io.github.maheevil.modbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.modules.extra.phishing.extPhishing
import com.kotlindiscord.kord.extensions.utils.env
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.entity.Snowflake
import io.github.maheevil.modbot.extensions.moderation.auto_moderation.AntiScamProt
import io.github.maheevil.modbot.extensions.moderation.util.ModerationCommands
import io.github.maheevil.modbot.extensions.moderation.auto_moderation.RaidProt
import io.github.maheevil.modbot.extensions.moderation.logging.LogEventListener
import io.github.maheevil.modbot.extensions.util.MiscCommands
import io.github.maheevil.modbot.extensions.util.suggestions.SuggestionCommand
import io.github.maheevil.modbot.util.config.GuildConfigData
import io.github.maheevil.modbot.util.config.deserializeAndLoadFromJson
import io.github.maheevil.modbot.util.config.serializeAndSaveToJson
import io.ktor.client.features.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

var guildConfigDataMap = HashMap<Long,GuildConfigData>()

val TEST_SERVER_ID = Snowflake(env("TEST_SERVER").toLong())
var joinLeaveLogChannelID: Snowflake = Snowflake(env("JOIN_LEAVE_LOG").toLong())
var modLogsChannelID: Snowflake = Snowflake(env("MOD_LOGS").toLong())
var alertLogsChannelID: Snowflake = Snowflake(env("ALERT_LOGS").toLong())
var inviteCode: String = env("INVITE")
private val TOKEN = env("TOKEN")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        chatCommands {
            defaultPrefix = "?"
            enabled = true

            prefix { default ->
                if (guildId == TEST_SERVER_ID) "!" else default
            }
        }
        extensions {
            add(::AntiScamProt)
            add(::ModerationCommands)
            add(::RaidProt)
            add(::LogEventListener)
            add(::SuggestionCommand)
            add(::MiscCommands)

            // KordEx extra modules
            extMappings {}
            extPhishing {
                appName = "Untitled-Moderation-Bot"
            }
        }
    }

    //guildConfigDataMap[TEST_SERVER_ID.toLong()] = GuildConfigData(joinLeaveLogChannelID, alertLogsChannelID, modLogsChannelID, inviteCode )
    deserializeAndLoadFromJson()
    serializeAndSaveToJson()
    bot.start()
}