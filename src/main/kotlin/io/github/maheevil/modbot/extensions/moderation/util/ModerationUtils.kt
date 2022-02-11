package io.github.maheevil.modbot.extensions.moderation.util

import com.kotlindiscord.kord.extensions.utils.hasPermission
import com.kotlindiscord.kord.extensions.utils.respond
import com.kotlindiscord.kord.extensions.utils.timeoutUntil
import dev.kord.common.Color
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.*
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.GuildMessageChannel
import io.github.maheevil.modbot.extensions.moderation.logging.createModLog
import io.github.maheevil.modbot.modLogsChannelID
import kotlinx.datetime.Clock
import kotlin.time.Duration

suspend fun createBanWithLog(meessage: Message,guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, banReason: String?){
    guild.ban(target){reason = banReason}
    meessage.respond(
            "Banned ${guild.kord.getUser(target)?.mention}, Reason given : $banReason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"banned",moderator.id,target,banReason, Color(0xff0000))
}

suspend fun removeBanWithLog(meessage: Message,guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, unbanReason: String?){
    guild.unban(target,unbanReason)
    meessage.respond(
            "Unbanned ${guild.kord.getUser(target)?.mention}, Reason given : $unbanReason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"unbanned",moderator.id,target,unbanReason, Color(0x09850b))
}

suspend fun kickUserWithLog(meessage: Message?, guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, kickReason: String?){
    guild.kick(target,kickReason)
    meessage?.respond(
            "Kicked ${guild.kord.getUser(target)?.mention}, Reason given : $kickReason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"kicked",moderator.id,target,kickReason, Color(0xff5e00))
}

suspend fun timeoutUserWithLog(meessage: Message?, guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, duration: Duration, reason: String?){
    guild.getMember(target).edit {
        this.reason = reason
        timeoutUntil =  Clock.System.now() + duration
    }
    meessage?.respond(
            "timedout ${guild.kord.getUser(target)?.mention},Duration = $duration, Reason given : $reason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"timedout",moderator.id,target,reason, Color(0xd9d904),duration)
}

suspend fun untimeoutUserWithLog(meessage: Message?, guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, reason: String?){
    guild.getMember(target).edit {
        this.reason = reason
        timeoutUntil = null
    }
    meessage?.respond(
            "untimedout ${guild.kord.getUser(target)?.mention}, Reason given : $reason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"timeoutn't",moderator.id,target,reason, Color(0x55ff00))
}

suspend fun verifyModCommand(guild: GuildBehavior?, message: Message, target: Snowflake, user: Snowflake, permission: Permission) : Boolean {
    if(guild == null) return false

    val targetAsMember = guild.getMemberOrNull(target)

    if (targetAsMember == null) {
        message.respond("The user is not in this Guild/Server")
        return false
    }else if (targetAsMember.hasPermission(permission)) {
        message.respond("That user is a moderator")
        return false
    }else if(user == target){
        message.respond("Don't execute a moderation command on yourself")
        return false
    }

    return true
}