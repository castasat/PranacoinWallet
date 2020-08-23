package com.openyogaland.denis.pranacoin_wallet_2_0.architecture

@Suppress("MemberVisibilityCanBePrivate")
class EventWrapper<out TYPE>(private val event: TYPE) {
    var eventHasBeenHandled = false
        private set // external read-only, internal writable

    // returns error and prevents it's use again
    fun getEventIfNotHandled(): TYPE? =
        when (eventHasBeenHandled) {
            true -> null
            else -> {
                eventHasBeenHandled = true
                event
            }
        }

    // returns event even if it has already been handled
    fun getAlreadyHandledEvent(): TYPE = event
}