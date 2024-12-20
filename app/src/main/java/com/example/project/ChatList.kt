package com.example.project

/**
 * Represents a chat list with a unique identifier.
 * This class is used to manage and store the ID associated with a chat list.
 */
class ChatList {
    private lateinit var id: String

    /**
     * Default constructor.
     * Initializes a new instance of the ChatList class without an ID.
     */
    constructor()

    /**
     * Constructor with a parameter to initialize the chat list with a given ID.
     *
     * @param id The unique identifier for the chat list.
     */

    constructor(id: String) {
        this.id = id
    }

    /**
     * Returns the unique ID associated with the chat list.
     *
     * @return The ID of the chat list.
     */
    fun getId(): String {
        return id
    }

    /**
     * Sets the unique ID for the chat list.
     *
     * @param id The unique identifier to set for the chat list.
     */
    fun setId(id: String){
        this.id=id
    }
}