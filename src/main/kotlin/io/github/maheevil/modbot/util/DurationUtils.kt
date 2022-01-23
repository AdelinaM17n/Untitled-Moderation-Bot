package io.github.maheevil.modbot.util

import kotlin.time.Duration

fun durationToHuman(duration: Duration) : String{
    val minutes = duration.inWholeMinutes % 60
    val hours = duration.inWholeHours % 24
    val days = duration.inWholeDays

    return "${ if(days > 0) "$days Day${ if(days < 2) " " else "s "}" else ""}${ if(hours > 0) "$hours Hour${ if(hours < 2) " " else "s "}" else "" }${ if(minutes > 0) "$minutes Minute${ if(minutes < 2) "" else "s"}" else "" }"
}

/*
var durationInHuman = ""

    if(days > 0)
        durationInHuman = "$days Day${if(days < 2) " " else "s "}"
    if(hours > 0)
        durationInHuman = "$durationInHuman$hours Hour${if(hours < 2) " " else "s "}"
    if(minutes > 0)
        durationInHuman = "$durationInHuman$minutes Minute${if(minutes < 2) "" else "s"}"
*/