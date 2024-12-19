package com.example.project

class ChatList {
    private lateinit var id: String

    constructor()

    constructor(id: String) {
        this.id = id
    }

    fun getId(): String {
        return id
    }

    fun setId(id: String){
        this.id=id
    }
}