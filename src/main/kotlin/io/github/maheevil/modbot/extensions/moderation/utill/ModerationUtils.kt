package io.github.maheevil.modbot.extensions.moderation.utill

import com.kotlindiscord.kord.extensions.commands.Arguments
import com.kotlindiscord.kord.extensions.commands.converters.impl.defaultingCoalescingString
import com.kotlindiscord.kord.extensions.commands.converters.impl.snowflake
import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.chatCommand
import com.kotlindiscord.kord.extensions.utils.respond
import dev.kord.common.entity.Permission
import dev.kord.core.behavior.ban

class ModerationUtils : Extension() {
    override val name = "moderation"

    override suspend fun setup() {
        chatCommand(::ModCommandArgs) {
            name = "ban"
            description = "Bans the user."
            action {
                if (user == null || guild == null)
                    return@action

                if (guild?.getMemberOrNull(user!!.id)?.getPermissions()?.contains(Permission.BanMembers) != true){
                    message.respond("You don't have permissions for this action")
                    return@action
                }else if(guild?.getMemberOrNull(arguments.target)?.getPermissions()?.contains(Permission.BanMembers) == true){
                    message.respond("The bot cannot ban a other moderator/admin")
                    return@action
                }

                createBanWithLog(message,guild!!,user!!,arguments.target, arguments.reason)
            }
        }

        chatCommand(::ModCommandArgs) {
            name = "unban"
            description = "Unbans the user."
            action {
                if (user == null || guild == null)
                    return@action

                if (guild?.getMember(user!!.id)?.getPermissions()?.contains(Permission.BanMembers) != true){
                    message.respond("You don't have permissions for this action")
                    return@action
                }else if(guild?.getBanOrNull(arguments.target) == null){
                    message.respond("The user is not banned")
                    return@action
                }

                removeBanWithLog(message,guild!!,user!!,arguments.target, arguments.reason)
            }
        }

        chatCommand(::ModCommandArgs) {
            name = "kick"
            description = "Kicks the user."
            action {
                if (user == null || guild == null)
                    return@action

                if (guild?.getMember(user!!.id)?.getPermissions()?.contains(Permission.KickMembers) != true){
                    message.respond("You don't have permissions for this action")
                    return@action
                }else if(guild?.getMemberOrNull(arguments.target) == null){
                    message.respond("The user is not in this Guild/Server")
                    return@action
                }else if(guild?.getMember(arguments.target)?.getPermissions()?.contains(Permission.KickMembers) == true){
                    message.respond("The bot cannot kick a other moderator/admin")
                    return@action
                }

                kickUserWithLog(message,guild!!,user!!,arguments.target, arguments.reason)
            }
        }
    }
    inner class ModCommandArgs : Arguments() {
        val target by snowflake("target", description = "Person you want to ban/kick/unban")

        val reason by defaultingCoalescingString(
                "reason",
                description = "Reason fo the action",
                defaultValue = "No reason given"
        )
    }

}