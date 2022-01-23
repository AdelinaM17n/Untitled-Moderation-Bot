package io.github.maheevil.modbot.extensions.moderation.util

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingDefaultingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.coalescingOptionalDuration
import com.kotlindiscord.kord.extensions.commands.converters.impl.snowflake
import com.kotlindiscord.kord.extensions.commands.converters.impl.string
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.utils.respond
import com.kotlindiscord.kord.extensions.utils.timeoutUntil
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.edit
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

class ModerationCommands : Extension() {
    override val name = "moderation"

    override suspend fun setup() {
        chatCommand(::ModCommandArgs) {
            name = "ban"
            description = "Bans the user."
            requiredPerms.add(Permission.BanMembers)

            action {
                if (user == null || guild == null)
                    return@action

                if(guild?.getMemberOrNull(arguments.target)?.getPermissions()?.contains(Permission.BanMembers) == true){
                    message.respond("The bot cannot ban a other moderator/admin")
                    return@action
                }

                createBanWithLog(message,guild!!,user!!,arguments.target, arguments.reason)
            }
        }

        chatCommand(::ModCommandArgs) {
            name = "unban"
            description = "Unbans the user."
            requiredPerms.add(Permission.BanMembers)

            action {
                if (user == null || guild == null)
                    return@action

               if(guild?.getBanOrNull(arguments.target) == null){
                    message.respond("The user is not banned")
                    return@action
               }

                removeBanWithLog(message,guild!!,user!!,arguments.target, arguments.reason)
            }
        }

        chatCommand(::ModCommandArgs) {
            name = "kick"
            description = "Kicks the user."
            requiredPerms.add(Permission.KickMembers)

            action {
                if (user == null || guild == null)
                    return@action

               if(guild?.getMemberOrNull(arguments.target) == null){
                    message.respond("The user is not in this Guild/Server")
                    return@action
               }else if(guild?.getMember(arguments.target)?.getPermissions()?.contains(Permission.KickMembers) == true){
                    message.respond("The bot cannot kick a other moderator/admin")
                    return@action
               }

                kickUserWithLog(message,guild!!,user!!,arguments.target, arguments.reason)
            }
        }

        chatCommand(::DuratedModCommandArgs) {
            name = "timeout"
            description = "Kicks the user."
            requiredPerms.add(Permission.KickMembers)

            action {
                if (user == null || guild == null)
                    return@action

                if(guild?.getMemberOrNull(arguments.target) == null){
                    message.respond("The user is not in this Guild/Server")
                    return@action
                }

                timeoutUserWithLog(message,guild!!,user!!,arguments.target,Duration.parse(arguments.duration),arguments.reason)
            }
        }

    }
    inner class ModCommandArgs : Arguments() {
        val target by snowflake{
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
        val target by snowflake {
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