package com.example.blog

class UserModel {
    var uid: String?=null
    var name: String?=null
    var email: String?=null
    var postImage: String?=null
    var postId: String?=null
    var postTitle: String?=null
    var postContent: String?=null
    var postDate: String?=null


    constructor(){}

    constructor(postId:String?,postTitle:String?,postContent:String?,postDate:String?,name:String?,postImage:String?, uid:String?){
        this.postId = postId
        this.postTitle = postTitle
        this.postContent = postContent
        this.postDate = postDate
        this.name = name
        this.postImage = postImage
        this.uid = uid
    }


    constructor(uid:String?, name:String?, email:String?){
        this.uid = uid
        this.name = name
        this.email = email

    }
}