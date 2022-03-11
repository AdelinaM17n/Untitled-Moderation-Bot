/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.maheevil.modbot.extensions.moderation

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.entity.Permission
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import io.github.maheevil.modbot.extensions.logging.createAlertLog
import io.github.maheevil.modbot.guildConfigDataMap

class RaidProt : Extension() {
    override val name = "raidprot"
    override suspend fun setup() {
        event<MessageCreateEvent>{
            action{
                val guild: Guild = event.getGuild() ?: return@action
                val raidPingCount = guildConfigDataMap[guild.id.toLong()]?.raidPingCount ?: return@action

                if(event.message.author == null || event.message.getAuthorAsMember()?.getPermissions()?.contains(Permission.MentionEveryone) == true)
                    return@action

                if(event.message.mentionedUserIds.count() > raidPingCount){
                    event.message.delete()

                    val kickReason = "Anti-Raid: More than $raidPingCount users pinged in one message"

                    kickUserWithLog(null,guild,event.kord.getSelf(),event.message.author!!,kickReason)
                    createAlertLog(
                        guild.getChannel(guildConfigDataMap[guild.id.toLong()]?.modLogsChannel ?: return@action) as GuildMessageChannel,
                        event.message.author!!,event.message.content, "Auto-Anti-Raid Alert"
                    )
                }
            }
        }
    }
}

