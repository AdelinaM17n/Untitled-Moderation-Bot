package io.github.maheevil.modbot.extensions.moderation.logging

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.timeoutUntil
import dev.kord.common.Color
import dev.kord.common.entity.AuditLogEvent
import dev.kord.core.behavior.getAuditLogEntries
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.guild.*
import io.github.maheevil.modbot.joinLeaveLogChannelID
import io.github.maheevil.modbot.modLogsChannelID
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@OptIn(kotlin.time.ExperimentalTime::class)
class LogEventListener : Extension() {
    override val name = "log"

    override suspend fun setup() {
        event<BanAddEvent> {
            action{
                val ban = event.guild.getAuditLogEntries { AuditLogEvent.MemberBanAdd }
                        .filter {it.targetId == event.getBan().userId && it.reason == event.getBan().reason
                                && Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60}.firstOrNull()

                if(ban?.targetId == null || ban.userId != ban.kord.selfId) return@action

                createModLog(event.guild.getChannel(modLogsChannelID) as GuildMessageChannel,"banned", ban.userId, ban.targetId!!, ban.reason, Color(0xff0000))
            }
        }

        event<BanRemoveEvent> {
            action {
                val unban = event.guild.getAuditLogEntries { AuditLogEvent.MemberBanRemove }
                        .filter {it.targetId == event.user.id && Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60 }.firstOrNull()

                if(unban?.targetId == null || unban.userId != unban.kord.selfId) return@action

                createModLog(event.guild.getChannel(modLogsChannelID) as GuildMessageChannel,"unbanned", unban.userId, unban.targetId!!, unban.reason, Color(0x09850b))
            }
        }

        event<MemberLeaveEvent> {
            action{
                val kick = event.guild.getAuditLogEntries { AuditLogEvent.MemberKick }
                        .filter { it.targetId == event.user.id && Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60}.firstOrNull()

                if(kick?.targetId != null && kick.userId != kick.kord.selfId && event.guild.getBanOrNull(kick.targetId!!) == null){
                    createModLog(event.guild.getChannel(modLogsChannelID) as GuildMessageChannel,"kicked",kick.userId,kick.targetId!!,kick.reason, Color(0xff5e00))
                }

                createJoinLeaveLog(event.guild.getChannel(joinLeaveLogChannelID) as GuildMessageChannel,false,event.user)
            }
        }

        event<MemberJoinEvent> {
            action {
                createJoinLeaveLog(event.guild.getChannel(joinLeaveLogChannelID) as GuildMessageChannel,true,event.member.asUser())
            }
        }

        event<MemberUpdateEvent> {
            action {
                if(event.old?.timeoutUntil == event.member.timeoutUntil) return@action

                val auditEntry = event.guild.getAuditLogEntries { AuditLogEvent.MemberUpdate }
                        .filter { it.targetId == event.member.id &&  Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60 }.firstOrNull()

                if(auditEntry?.userId == auditEntry?.kord?.selfId || auditEntry?.targetId == null) return@action

                val isTimedouted = event.member.timeoutUntil != null
                createModLog(
                        event.guild.getChannel(modLogsChannelID) as GuildMessageChannel,
                        if(isTimedouted) "timedout" else "timedoutn't", auditEntry.userId, auditEntry.targetId!!, auditEntry.reason, Color(0x09850b),
                        if(isTimedouted) event.member.timeoutUntil!!.minus(Clock.System.now()).plus(1.minutes) else null
                )
            }
        }
    }
}

