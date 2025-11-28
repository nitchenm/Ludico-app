package com.example.ludico_app.data.model

data class Post(
    val userId: Int,     //ID DEL USUARIO CREADOR DEL POST
    val id: Int,        //ID DEL POST
    val title: String,  //TITULO DE POST
    val body: String    //CUERPO O CONTENIDO DEL POST
)
