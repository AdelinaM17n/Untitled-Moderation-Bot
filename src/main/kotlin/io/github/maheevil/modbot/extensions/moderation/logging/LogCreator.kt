package io.github.maheevil.modbot.extensions.moderation.logging

import com.kotlindiscord.kord.extensions.time.TimestampType
import com.kotlindiscord.kord.extensions.time.toDiscord
import dev.kord.common.Color
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildMessageChannel
import io.github.maheevil.modbot.util.durationToHuman
import kotlinx.datetime.Clock
import kotlin.time.Duration

/** Reference for future me (color codes)
 * kicked - 0xff5e00
 * muted - 0xd9d904
 * unbanned - 0x09850b
 * unmuted - 0x55ff00
 */

suspend fun createModLog(channel: GuildMessageChannel, modAction: String, moderator: User, target: User, reason: String?, colour: Color, duration: Duration? = null){
    channel.createEmbed {
        title = "Member $modAction!"
        color = colour
        field("User",false){"<@${target.id}> `${target.mention}`"}
        field("Reason",false) {"`${reason ?: "No reason given"}`"}
        if(duration != null) {
            field("Duration", true){durationToHuman(duration)}
            field("Until",true){(Clock.System.now() + duration).toDiscord(TimestampType.ShortDateTime)}
        }
        field("Moderator",false){ moderator.mention }
        timestamp = Clock.System.now()
    }
}

suspend fun createAlertLog(channel: GuildMessageChannel,user: User,msgContent: String, alertType: String){
    channel.createEmbed {
        title = alertType
        color = Color(0xff0000)
        field("User",false){"${user.mention} `${user.username}#${user.discriminator}`"}
        field("Message Content",false){"`$msgContent`"}
        footer { text = "The message has been deleted and the user has been kicked" }
        timestamp = Clock.System.now()
    }
}

suspend fun createJoinLeaveLog(channel: GuildMessageChannel, joined: Boolean, user: User){
    channel.createEmbed {
        title = "Member ${if(joined) "joined" else "left"}"
        color = if(joined) Color(0x09850b) else Color(0xff0000)
        field("User",false){ "${user.mention} `${user.username}#${user.discriminator}`" }
        timestamp = Clock.System.now()
    }
}