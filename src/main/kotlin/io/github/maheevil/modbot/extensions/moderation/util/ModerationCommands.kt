package io.github.maheevil.modbot.extensions.moderation.util

import com.kotlindiscord.kord.extensions.checks.hasPermission
import com.kotlindiscord.kord.extensions.checks.isNotBot
import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingDefaultingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.commands.converters.impl.user
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.soywiz.korio.dynamic.KDynamic.Companion.toLong
import dev.kord.common.entity.Permission
import kotlin.time.Duration

class ModerationCommands : Extension() {
    override val name = "moderation"

    override suspend fun setup() {
        chatCommand(::ModCommandArgs) {
            name = "ban"
            description = "Bans the user."
            requiredPerms.add(Permission.BanMembers)

            check { hasPermission(Permission.BanMembers) }
            check { isNotBot() }

            action {
                if(!verifyModCommand(guild,message,arguments.target.id, Permission.BanMembers))
                    return@action
                createBanWithLog(message,guild!!,user!!,arguments.target.id, arguments.reason)
            }
        }

        chatCommand(::ModCommandArgs) {
            name = "unban"
            description = "Unbans the user."
            requiredPerms.add(Permission.BanMembers)

            check { hasPermission(Permission.BanMembers) }
            check { isNotBot() }

            action {
                if(!verifyModCommand(guild,message,arguments.target.id, Permission.BanMembers))
                    return@action
                removeBanWithLog(message,guild!!,user!!,arguments.target.id, arguments.reason)
            }
        }

        chatCommand(::ModCommandArgs) {
            name = "kick"
            description = "Kicks the user."
            requiredPerms.add(Permission.KickMembers)

            check { hasPermission(Permission.KickMembers) }
            check { isNotBot() }

            action {
                if(!verifyModCommand(guild,message,arguments.target.id, Permission.KickMembers, true))
                    return@action
                kickUserWithLog(message,guild!!,user!!,arguments.target.id, arguments.reason)
            }
        }

        chatCommand(::DuratedModCommandArgs) {
            name = "timeout"
            description = "timeouts the user."
            requiredPerms.add(Permission.ModerateMembers)

            check { hasPermission(Permission.ModerateMembers) }
            check { isNotBot() }

            action {
                if(!verifyModCommand(guild,message,arguments.target.id, Permission.ModerateMembers,true))
                    return@action
                timeoutUserWithLog(message,guild!!,user!!,arguments.target.id,Duration.parse(arguments.duration),arguments.reason)
            }
        }

        chatCommand(::ModCommandArgs) {
            name = "untimeout"
            description = "untimeouts the user."
            requiredPerms.add(Permission.ModerateMembers)

            check { hasPermission(Permission.ModerateMembers) }
            check { isNotBot() }

            action {
                if(!verifyModCommand(guild,message,arguments.target.id, Permission.ModerateMembers,true))
                    return@action
                untimeoutUserWithLog(message, guild!!, user!!, arguments.target.id, arguments.reason)
            }
        }
    }
    inner class ModCommandArgs : Arguments() {
        val target by user{
            name = "target"
            description = "Person you want to ban/kick/unban"
        }

        val reason by coalescingDefaultingString{
                name = "reason"
                description = "Reason fo the action"
                defaultValue = "No reason given"
        }
    }

    inner class DuratedModCommandArgs : Arguments() {
        val target by user {
            name = "target"
            description = "The target, what else did you expect?"
        }

        val duration by string {
            name = "duration"
            description = "Duration."

        }

        val reason by coalescingDefaultingString{
            name = "reason"
            description = "Reason fo the action"
            defaultValue = "No reason given"
        }
    }

}