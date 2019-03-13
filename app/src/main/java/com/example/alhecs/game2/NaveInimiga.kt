package com.example.alhecs.game2

class NaveInimiga(larguraTela: Int, alturaTela: Int){
    var life = Math.ceil(Math.random() * 2).toInt() + 1

    var posX = (Math.random() * (larguraTela - naveInimigaW)).toFloat() % larguraTela
    var posY = (-naveInimigaH).toFloat()

    var acelX = 0f
    var acelY = (Math.ceil(Math.random() * 10).toInt() + 5).toFloat()

    var timeC = System.currentTimeMillis()
    var score = life * 10
}