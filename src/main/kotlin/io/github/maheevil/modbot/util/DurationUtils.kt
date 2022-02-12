package io.github.maheevil.modbot.util

import kotlin.time.Duration

fun durationToHuman(duration: Duration) : String{
    val minutes = duration.inWholeMinutes % 60 ; val hours = duration.inWholeHours % 24 ; val days = duration.inWholeDays

    return duration.toString()
            .replace("d", if(days > 1) " days " else " day ")
            .replace("h",if(hours > 1) " hours " else " hour ")
            .replace("m",if(minutes > 1)" minutes " else " minute ")
}

/*
fun durationToHuman(duration: Duration) : String{
    val minutes = duration.inWholeMinutes % 60
    val hours = duration.inWholeHours % 24
    val days = duration.inWholeDays

    return "${ if(days > 0) "$days Day${ if(days < 2) " " else "s "}" else ""}${ if(hours > 0) "$hours Hour${ if(hours < 2) " " else "s "}" else "" }${ if(minutes > 0) "$minutes Minute${ if(minutes < 2) "" else "s"}" else "" }"
}
*/
