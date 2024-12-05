package com.example.project

import java.util.Date

data class TekstMessage(val taxt: String,
                        override val time: Date,
                        override val senderId: String,
                        override val type: String = MessageType.TEXT) : Message {
    constructor() : this("", Date(0), "")
}