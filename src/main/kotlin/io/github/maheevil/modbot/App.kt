package io.github.maheevil.modbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import io.github.maheevil.modbot.extensions.moderation.auto_moderation.AntiScamProt
import io.github.maheevil.modbot.extensions.moderation.util.ModerationCommands
import io.github.maheevil.modbot.extensions.moderation.auto_moderation.RaidProt
import io.github.maheevil.modbot.extensions.moderation.logging.LogEventListener
import io.github.maheevil.modbot.extensions.util.suggestions.SuggestionCommand

val TEST_SERVER_ID = Snowflake(env("TEST_SERVER").toLong())
var joinLeaveLogChannelID: Snowflake = Snowflake(env("JOIN_LEAVE_LOG").toLong())
var modLogsChannelID: Snowflake = Snowflake(env("MOD_LOGS").toLong())
var alertLogsChannelID: Snowflake = Snowflake(env("ALERT_LOGS").toLong())

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
        }
    }

    bot.start()
}
