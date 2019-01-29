package com.habeshastudio.mahder.model

/**
 * Created by kibrom on 3/31/2018.
 */

class Users {

    lateinit var name: String
    lateinit var image: String
    lateinit var status: String
    lateinit var votes: String
    lateinit var followers: String
    lateinit var answers: String
    lateinit var city: String
    lateinit var thumb_image: String

    constructor()

    constructor(votes: String, followers: String, answers: String, city: String, name: String, image: String, status: String, thumb_image: String) {
        this.name = name
        this.answers = answers
        this.city = city
        this.votes = votes
        this.followers = followers
        this.image = image
        this.status = status
        this.thumb_image = thumb_image
    }

    constructor(name: String, image: String, status: String, thumb_image: String) {
        this.name = name
        this.answers = answers
        this.city = city
        this.votes = votes
        this.followers = followers
        this.image = image
        this.status = status
        this.thumb_image = thumb_image
    }
}
