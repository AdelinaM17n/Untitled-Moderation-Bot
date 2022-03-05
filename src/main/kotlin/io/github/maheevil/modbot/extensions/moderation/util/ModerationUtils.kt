package io.github.maheevil.modbot.extensions.moderation.util

import com.kotlindiscord.kord.extensions.utils.hasPermission
import com.kotlindiscord.kord.extensions.utils.respond
import com.kotlindiscord.kord.extensions.utils.timeoutUntil
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.Color
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildMessageChannel
import io.github.maheevil.modbot.extensions.moderation.logging.createModLog
import io.github.maheevil.modbot.guildConfigDataMap
import kotlinx.datetime.Clock
import kotlin.time.Duration

suspend fun createBanWithLog(meessage: Message,guild: GuildBehavior, moderator: UserBehavior, targetUser: User, banReason: String?){
    if(guild.getBanOrNull(targetUser.id) != null){
        meessage.respond("The member is already banned")
        return
    }
    guild.ban(targetUser.id){reason = banReason}
    meessage.respond(
            "Banned ${targetUser.mention}, Reason given : $banReason"
    )
    createModLog(
        guild.getChannel(guildConfigDataMap[guild.id.toLong()]?.modLogsChannel ?: return) as GuildMessageChannel,
        "banned",
        moderator.asUser(),
        targetUser,
        banReason,
        Color(0xff0000)
    )
}

suspend fun removeBanWithLog(meessage: Message,guild: GuildBehavior, moderator: UserBehavior, targetUser: User, unbanReason: String?){
    if(guild.getBanOrNull(targetUser.id) == null){
        meessage.respond("The member is not banned")
        return
    }
    guild.unban(targetUser.id,unbanReason)
    meessage.respond(
            "Unbanned ${targetUser.mention}, Reason given : $unbanReason"
    )
    createModLog(
        guild.getChannel(guildConfigDataMap[guild.id.toLong()]?.modLogsChannel ?: return) as GuildMessageChannel,
        "unbanned",
        moderator.asUser(),
        targetUser,
        unbanReason,
        Color(0x09850b)
    )
}

suspend fun kickUserWithLog(meessage: Message?, guild: GuildBehavior, moderator: UserBehavior, targetUser: User, kickReason: String?){
    guild.kick(targetUser.id,kickReason)
    meessage?.respond(
            "Kicked ${targetUser.mention}, Reason given : $kickReason"
    )
    createModLog(
        guild.getChannel(guildConfigDataMap[guild.id.toLong()]?.modLogsChannel ?: return) as GuildMessageChannel,
        "kicked",
        moderator.asUser(),
        targetUser,
        kickReason,
        Color(0xff5e00)
    )
}

suspend fun timeoutUserWithLog(meessage: Message?, guild: GuildBehavior, moderator: UserBehavior, targetUser: User, duration: Duration, reason: String?){
    guild.getMember(targetUser.id).edit {
        this.reason = reason
        timeoutUntil =  Clock.System.now() + duration
    }
    meessage?.respond(
            "timedout ${targetUser.mention},Duration = $duration, Reason given : $reason"
    )
    createModLog(
        guild.getChannel(guildConfigDataMap[guild.id.toLong()]?.modLogsChannel ?: return) as GuildMessageChannel,
        "timedout",
        moderator.asUser(),
        targetUser,
        reason,
        Color(0xd9d904),
        duration
    )
}

suspend fun untimeoutUserWithLog(meessage: Message?, guild: GuildBehavior, moderator: UserBehavior, targetUser: User, reason: String?){
    guild.getMember(targetUser.id).edit {
        this.reason = reason
        timeoutUntil = null
    }
    meessage?.respond(
            "untimedout ${targetUser.mention}, Reason given : $reason"
    )
    createModLog(guild.getChannel(
        guildConfigDataMap[guild.id.toLong()]?.modLogsChannel ?: return) as GuildMessageChannel,
        "timeoutn't",
        moderator.asUser(),
        targetUser,
        reason,
        Color(0x55ff00)
    )
}

suspend fun verifyModCommand(guild: GuildBehavior, message: Message, target: Snowflake, permission: Permission,targetPresenceRequired: Boolean = false) : Boolean {
    val targetAsMember = guild.getMemberOrNull(target)

    if (targetAsMember == null && targetPresenceRequired) {
        message.respond("The user is not in this Guild/Server")
        return false
    }else if (targetAsMember?.hasPermission(permission) == true) {
        message.respond("That user is a moderator")
        return false
    }

    return true
}