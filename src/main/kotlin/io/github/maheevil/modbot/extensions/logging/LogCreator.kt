/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */
package io.github.maheevil.modbot.extensions.logging

import com.kotlindiscord.kord.extensions.time.TimestampType
import com.kotlindiscord.kord.extensions.time.toDiscord
import dev.kord.common.Color
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.GuildMessageChannel
import kotlinx.datetime.Clock
import kotlin.time.Duration

/** Reference for future me (color codes)
 * kicked - 0xff5e00
 * muted - 0xd9d904
 * unbanned - 0x09850b
 * unmuted - 0x55ff00
 */

suspend fun createModLog(channel: GuildMessageChannel, modAction: String, moderator: User?, target: User, reason: String?, colour: Color, duration: Duration? = null){
    channel.createEmbed {
        title = "Member $modAction!"
        color = colour
        field("User",false){"<@${target.id}> `${target.mention}`"}
        field("Reason",false) {"`${reason ?: "No reason given"}`"}
        if(duration != null) {
            field("Duration", true){ durationToHuman(duration) }
            field("Until",true){(Clock.System.now() + duration).toDiscord(TimestampType.ShortDateTime)}
        }
        field("Moderator",false){ moderator?.mention ?: "Moderator is gone, reduced to atoms" }
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

fun durationToHuman(duration: Duration) : String{
    val minutes = duration.inWholeMinutes % 60 ; val hours = duration.inWholeHours % 24 ; val days = duration.inWholeDays

    return duration.toString()
            .replace("d", if(days.toInt() == 1) " day" else " days")
            .replace("h", if(hours.toInt() == 1) " hour" else " hours")
            .replace("m", if(minutes.toInt() == 1) " minute" else " minutes")
}