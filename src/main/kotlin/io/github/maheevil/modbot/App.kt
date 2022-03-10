/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.maheevil.modbot

import com.kotlindiscord.kord.extensions.ExtensibleBot
import com.kotlindiscord.kord.extensions.modules.extra.mappings.extMappings
import com.kotlindiscord.kord.extensions.modules.extra.phishing.extPhishing
import com.kotlindiscord.kord.extensions.utils.env
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.entity.Snowflake
import io.github.maheevil.modbot.extensions.moderation.RaidProt
import io.github.maheevil.modbot.extensions.logging.LogEventListener
import io.github.maheevil.modbot.extensions.moderation.ModerationCommands
import io.github.maheevil.modbot.extensions.util.MiscCommands
import io.github.maheevil.modbot.extensions.util.SuggestionCommand
import io.github.maheevil.modbot.extensions.util.config.GuildConfigData
import io.github.maheevil.modbot.extensions.util.config.deserializeAndLoadFromJson

var guildConfigDataMap = HashMap<Long, GuildConfigData>()

val TEST_SERVER_ID = Snowflake(env("TEST_SERVER").toLong())
private val TOKEN = env("TOKEN")

suspend fun main() {
    val bot = ExtensibleBot(TOKEN) {
        cache {
            cachedMessages = 2000
        }
        chatCommands {
            defaultPrefix = "!"
            enabled = true
        }
        applicationCommands {
            enabled = true
            defaultGuild(TEST_SERVER_ID)
        }
        extensions {
            add(::ModerationCommands)
            add(::RaidProt)
            add(::LogEventListener)
            add(::SuggestionCommand)
            add(::MiscCommands)

            // KordEx extra modules
            extMappings {}
            extPhishing {
                appName = "Untitled-Moderation-Bot"
                logChannelName = "alert-logs"
            }
        }
    }

    deserializeAndLoadFromJson()
    bot.start()
}