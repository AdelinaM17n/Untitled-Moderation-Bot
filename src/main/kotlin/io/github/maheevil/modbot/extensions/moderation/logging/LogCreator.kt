package io.github.maheevil.modbot.extensions.moderation.logging

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildMessageChannel
import kotlinx.datetime.Clock

/** Reference for future me (color codes)
 * kicked - 0xff5e00
 * muted - 0xd9d904
 * unbanned - 0x09850b
 * unmuted - 0x55ff00
 */

suspend fun createModLog(channel: GuildMessageChannel, modAction: String, moderator: Snowflake, target: Snowflake, reason: String?, colour: Color){
    val targetUser: User? = channel.kord.getUser(target)
    val targetUserNameWithDiscrim = "${targetUser?.username ?: "Deleted User"}#${targetUser?.discriminator ?: "0000"}"

    channel.createEmbed {
        title = "Member $modAction!"
        color = colour
        field("User",false){"<@${target.value}> `$targetUserNameWithDiscrim`"}
        field("Reason",false) {"`${reason ?: "No reason given"}`"}
        field("Moderator",false){"${channel.getGuild().getMemberOrNull(moderator)?.mention ?: moderator}"}
    }
}

suspend fun createAlertLog(){

}

suspend fun createJoinLeaveLog(channel: GuildMessageChannel, joined: Boolean, user: User){
    channel.createEmbed {
        title = "Member ${if(joined) "joined" else "left"}"
        color = if(joined) Color(0x09850b) else Color(0xff0000)
        field("User",false){ "${user.mention} `${user.username}#${user.discriminator}`" }
        timestamp = Clock.System.now()
    }
}