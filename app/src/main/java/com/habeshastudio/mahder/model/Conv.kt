package com.habeshastudio.mahder.model

class Conv {

    var isSeen: Boolean = false
    var timestamp: Long = 0

    constructor()

    constructor(seen: Boolean, timestamp: Long) {
        this.isSeen = seen
        this.timestamp = timestamp
    }
}
