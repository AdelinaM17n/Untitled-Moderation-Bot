package io.github.maheevil.modbot.extensions.moderation.logging

import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.channel.GuildMessageChannel

/** Reference for future me (color codes)
 * kicked - 0xff5e00
 * muted - 0xd9d904
 * unbanned - 0x09850b
 * unmuted - 0x55ff00
 */

suspend fun createModLog(channel: GuildMessageChannel, modAction: String, moderator: Snowflake, target: Snowflake, reason: String?, colour: Color){
    channel.createEmbed {
        title = "Member $modAction!"
        color = colour
        field("User",false){"<@${target.value}>"}
        field("Reason",false) {"`${reason ?: "No reason given"}`"}
        field("Moderator",false){"${channel.getGuild().getMemberOrNull(moderator)?.mention ?: moderator}"}
    }
}

suspend fun createAlertLog(){

}

suspend fun createJoinLeaveLog(){

}