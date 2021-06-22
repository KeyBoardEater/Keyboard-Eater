package com.key.struct.cor

interface Handler {
    fun handleRequest(handlerRequest: HandlerRequest)

    fun getLevel(): Int
}