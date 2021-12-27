package io.github.maheevil.modbot.extensions.moderation.utill

import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.ban
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.GuildMessageChannel
import io.github.maheevil.modbot.extensions.moderation.logging.createModLog
import io.github.maheevil.modbot.modLogsChannelID

suspend fun createBanWithLog(meessage: Message,guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, banReason: String?){
    guild.ban(target){reason = banReason}
    meessage.respond(
            "Banned ${guild.kord.getUser(target)?.mention}, Reason given : $banReason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"banned",moderator.id,target,banReason, Color(0xff0000))
}

suspend fun removeBanWithLog(meessage: Message,guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, unbanReason: String?){
    guild.ban(target){reason = unbanReason}
    meessage.respond(
            "Unbanned ${guild.kord.getUser(target)?.mention}, Reason given : $unbanReason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"unbanned",moderator.id,target,unbanReason, Color(0x09850b))
}

suspend fun kickUserWithLog(meessage: Message?, guild: GuildBehavior, moderator: UserBehavior, target: Snowflake, kickReason: String?){
    guild.ban(target){reason = kickReason}
    meessage?.respond(
            "Kicked ${guild.kord.getUser(target)?.mention}, Reason given : $kickReason"
    )
    createModLog(guild.getChannel(modLogsChannelID) as GuildMessageChannel,"kicked",moderator.id,target,kickReason, Color(0xff5e00))
}