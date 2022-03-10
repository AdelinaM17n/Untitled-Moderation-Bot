/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.maheevil.modbot.extensions.logging

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import com.kotlindiscord.kord.extensions.utils.timeoutUntil
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.Color
import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.getAuditLogEntries
import dev.kord.core.entity.channel.GuildMessageChannel
import dev.kord.core.event.guild.*
import io.github.maheevil.modbot.extensions.moderation.logging.createJoinLeaveLog
import io.github.maheevil.modbot.extensions.moderation.logging.createModLog
import io.github.maheevil.modbot.guildConfigDataMap
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

@OptIn(kotlin.time.ExperimentalTime::class)
class LogEventListener : Extension() {
    override val name = "log"

    override suspend fun setup() {
        event<BanAddEvent> {
            action{
                val modLogsSnowflake: Snowflake = guildConfigDataMap[event.guild.id.toLong()]?.modLogsChannel ?: return@action
                val ban = event.getBan()
                val auditLogEntry = event.guild.getAuditLogEntries { AuditLogEvent.MemberBanAdd }
                        .filter {it.targetId == ban.userId && it.reason == ban.reason
                                && Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60}.firstOrNull()

                if(auditLogEntry?.targetId == null || auditLogEntry.userId == auditLogEntry.kord.selfId) return@action

                createModLog(
                    event.guild.getChannel(modLogsSnowflake) as GuildMessageChannel,
                    "banned",
                    kord.getUser(auditLogEntry.userId),
                    ban.getUser(),
                    auditLogEntry.reason,
                    Color(0xff0000)
                )
            }
        }

        event<BanRemoveEvent> {
            action {
                val modLogsSnowflake: Snowflake = guildConfigDataMap[event.guild.id.toLong()]?.modLogsChannel ?: return@action
                val auditLogEntry = event.guild.getAuditLogEntries { AuditLogEvent.MemberBanRemove }
                        .filter {it.targetId == event.user.id && Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60 }.firstOrNull()

                if(auditLogEntry?.targetId == null || auditLogEntry.userId == auditLogEntry.kord.selfId) return@action

                createModLog(
                    event.guild.getChannel(modLogsSnowflake) as GuildMessageChannel,
                    "unbanned",
                    kord.getUser(auditLogEntry.userId),
                    event.user,
                    auditLogEntry.reason,
                    Color(0x09850b)
                )
            }
        }

        // There is no event for kicks so this is used for both leave and kick logs
        event<MemberLeaveEvent> {
            action{
                val modLogsSnowflake: Snowflake? = guildConfigDataMap[event.guild.id.toLong()]?.modLogsChannel
                if(modLogsSnowflake != null){
                    val auditLogEntry = event.guild.getAuditLogEntries { AuditLogEvent.MemberKick }
                            .filter { it.targetId == event.user.id && Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60}.firstOrNull()
                    // Banning a user also creates an invisible kick audit log entry so there is a check to see if the user is banned
                    if(auditLogEntry?.targetId != null && auditLogEntry.userId != auditLogEntry.kord.selfId && event.guild.getBanOrNull(auditLogEntry.targetId!!) == null){
                        createModLog(
                            event.guild.getChannel(modLogsSnowflake) as GuildMessageChannel,
                            "kicked",
                            kord.getUser(auditLogEntry.userId),
                            event.user,
                            auditLogEntry.reason,
                            Color(0xff5e00)
                        )
                    }
                }

                createJoinLeaveLog(
                    event.guild.getChannel(guildConfigDataMap[event.guild.id.toLong()]?.joinLeaveLogsChannel ?: return@action) as GuildMessageChannel,
                    false,
                    event.user
                )
            }
        }

        event<MemberJoinEvent> {
            action {
                createJoinLeaveLog(
                    event.guild.getChannel(guildConfigDataMap[event.guild.id.toLong()]?.joinLeaveLogsChannel ?: return@action) as GuildMessageChannel,
                    true,
                    event.member
                )
            }
        }

        // Listening the member update event to log timeout related things. This does NOT log timeouts running out.
        event<MemberUpdateEvent> {
            action {
                val modLogsSnowflake: Snowflake = guildConfigDataMap[event.guild.id.toLong()]?.modLogsChannel ?: return@action

                if(event.old?.timeoutUntil == event.member.timeoutUntil) return@action

                val auditEntry = event.guild.getAuditLogEntries { AuditLogEvent.MemberUpdate }
                        .filter { it.targetId == event.member.id &&  Clock.System.now().minus(it.id.timestamp).inWholeSeconds < 60 }.firstOrNull()
                if(auditEntry?.userId == auditEntry?.kord?.selfId || auditEntry?.targetId == null) return@action

                val isTimedouted = event.member.timeoutUntil != null
                createModLog(
                    event.guild.getChannel(modLogsSnowflake) as GuildMessageChannel,
                    if(isTimedouted) "timedout" else "timedoutn't",
                    kord.getUser(auditEntry.userId),
                    event.member,
                    auditEntry.reason,
                    Color(0x09850b),
                    if(isTimedouted) event.member.timeoutUntil!!.minus(Clock.System.now()).plus(1.minutes) else null //One minute added so logs will get the proper time. This does mean "until" will be not correct.
                )
            }
        }
    }
}

