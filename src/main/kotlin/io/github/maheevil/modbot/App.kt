package io.github.maheevil.modbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.modules.extra.phishing.extPhishing
import com.kotlindiscord.kord.extensions.utils.env
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.entity.Snowflake
import io.github.maheevil.modbot.extensions.moderation.auto_moderation.RaidProt
import io.github.maheevil.modbot.extensions.moderation.logging.LogEventListener
import io.github.maheevil.modbot.extensions.moderation.util.ModerationCommands
import io.github.maheevil.modbot.extensions.util.MiscCommands
import io.github.maheevil.modbot.extensions.util.suggestions.SuggestionCommand
import io.github.maheevil.modbot.util.config.GuildConfigData
import io.github.maheevil.modbot.util.config.deserializeAndLoadFromJson

var guildConfigDataMap = HashMap<Long,GuildConfigData>()

//val TEST_SERVER_ID = Snowflake(env("TEST_SERVER").toLong())
private val TOKEN = env("TOKEN")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        chatCommands {
            defaultPrefix = "!"
            enabled = true
        }
        extensions {
            add(::ModerationCommands)
            add(::RaidProt)
            add(::LogEventListener)
            add(::SuggestionCommand)
            add(::MiscCommands)

            // KordEx extra modules
            extMappings {}
            extPhishing { appName = "Untitled-Moderation-Bot" }
        }
    }

    deserializeAndLoadFromJson()
    bot.start()
}