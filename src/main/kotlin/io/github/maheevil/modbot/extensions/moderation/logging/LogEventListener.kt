package io.github.maheevil.modbot.extensions.moderation.logging

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.Color
import dev.kord.common.entity.AuditLogEvent
import dev.kord.core.behavior.getAuditLogEntries
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.BanRemoveEvent
import io.github.maheevil.modbot.modLogsChannelID
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

class LogEventListener : Extension() {
    override val name = "log"

    override suspend fun setup() {
        event<BanAddEvent> {
            action{
                val ban = event.guild.getAuditLogEntries { AuditLogEvent.MemberBanAdd }
                        .filter {it.targetId == event.getBan().userId && it.reason == event.getBan().reason }.first()

                if(ban.targetId == null || event.getGuild().getMemberOrNull(ban.userId)?.isBot == true) return@action

                createModLog(event.guild.getChannel(modLogsChannelID) as GuildMessageChannel,"banned", ban.userId, requireNotNull(ban.targetId), ban.reason, Color(0xff0000))
            }
        }

        event<BanRemoveEvent> {
            action {
                val unban = event.guild.getAuditLogEntries { AuditLogEvent.MemberBanRemove }
                        .filter {it.targetId == event.user.id}.first()
                if(unban.targetId == null || event.getGuild().getMemberOrNull(unban.userId)?.isBot == true) return@action

                createModLog(event.guild.getChannel(modLogsChannelID) as GuildMessageChannel,"unbanned", unban.userId, requireNotNull(unban.targetId), unban.reason, Color(0x09850b))
            }
        }
    }
}

