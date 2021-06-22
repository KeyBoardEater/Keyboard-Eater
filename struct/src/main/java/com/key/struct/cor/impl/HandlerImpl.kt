package com.key.struct.cor.impl

import com.key.struct.cor.Handler
import com.key.struct.cor.HandlerRequest
import com.key.struct.cor.InitException

abstract class HandlerImpl(handler: Handler) : Handler {

    private val nextHandler: Handler

    init {
        if (handler.getLevel() <= getLevel()) {
            throw InitException()
        }
        nextHandler = handler
    }

    override fun handleRequest(handlerRequest: HandlerRequest) {
        if (handlerRequest.getLevel() <= getLevel()) {
            realHandleRequest(handlerRequest)
        } else {
            nextHandler.handleRequest(handlerRequest)
        }
    }

    abstract fun realHandleRequest(handlerRequest: HandlerRequest)
}