package io.github.maheevil.modbot.extensions

import com.kotlindiscord.kord.extensions.extensions.Extension
import com.kotlindiscord.kord.extensions.extensions.event
import dev.kord.common.entity.Permission
import dev.kord.core.event.message.MessageCreateEvent
import java.util.regex.Pattern

class AntiScamProt : Extension() {
    override val name = "antiscam"
    private val urlPattern: Pattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
    )

    override suspend fun setup() {
        event<MessageCreateEvent> {
            action {
                val message = event.message

                if (event.getGuild() == null || message.author == null || message.author?.isBot == true || message.getAuthorAsMember()?.getPermissions()?.contains(Permission.MentionEveryone) == true)
                    return@action

                val mentionsEveryoneOrHere =
                        (message.content.contains("@here".toRegex()) && !message.content.contains("""\@here"""))
                        ||
                        (message.content.contains("@everyone".toRegex()) && !message.content.contains("""\@everyone"""))

                if (mentionsEveryoneOrHere && urlPattern.matcher(message.content).find()){
                    message.delete()
                    message.getAuthorAsMember()?.kick("Anti-Scam: Sending a message that mentions @here or @everyone with a link")
                }
            }
        }
    }
}

