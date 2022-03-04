package io.github.maheevil.modbot.extensions.moderation.auto_moderation

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.entity.Permission
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import io.github.maheevil.modbot.extensions.moderation.logging.createAlertLog
import io.github.maheevil.modbot.extensions.moderation.util.kickUserWithLog
import io.github.maheevil.modbot.guildConfigDataMap

class RaidProt : Extension() {
    override val name = "raidprot"
    override suspend fun setup() {
        event<MessageCreateEvent>{
            action{
                val guild: Guild = event.getGuild() ?: return@action

                if(event.message.author == null || event.message.getAuthorAsMember()?.getPermissions()?.contains(Permission.MentionEveryone) == true)
                    return@action

                if(event.message.mentionedUserIds.count() > 20 ){
                    event.message.delete()

                    val kickReason = "Anti-Raid: More than 20 users pinged in one message"

                    kickUserWithLog(null,guild,event.kord.getSelf(),event.message.author!!.id,kickReason)
                    createAlertLog(
                            guild.getChannel(guildConfigDataMap[guild.id.toLong()]?.modLogsChannel ?: return@action) as GuildMessageChannel,
                            event.message.author!!,event.message.content, "Auto-Anti-Raid Alert"
                    )
                }
            }
        }
    }
}

