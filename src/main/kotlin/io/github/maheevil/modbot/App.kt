package io.github.maheevil.modbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.utils.env
import dev.kord.common.entity.Snowflake
import io.github.maheevil.modbot.extensions.moderation.auto_moderation.AntiScamProt
import io.github.maheevil.modbot.extensions.moderation.utill.ModerationUtils
import io.github.maheevil.modbot.extensions.moderation.auto_moderation.RaidProt
import io.github.maheevil.modbot.extensions.moderation.logging.LogEventListener

val TEST_SERVER_ID = Snowflake(env("TEST_SERVER").toLong())
var joinLeaveLogChannelID: Snowflake = Snowflake(env("JOIN_LEAVE_LOG").toLong())
var modLogsChannelID: Snowflake = Snowflake(env("MOD_LOGS").toLong())
//var

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
            add(::ModerationUtils)
            add(::RaidProt)
            add(::LogEventListener)
        }
    }

    bot.start()
}
