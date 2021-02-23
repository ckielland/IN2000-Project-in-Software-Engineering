package com.example.chrysoum_oblig2

data class AlpacaParty
    (val id: String?, val name: String?, val leader: String?, val img: String?, val color: String?) {

    private var votes:Int = 0

    private var average:Double = 0.0

    fun setVotes(v: Int){
        votes = v
    }

    fun addVote(){
        votes += 1
    }

    fun getVotes() : Int{
        return votes
    }

    fun updateAverage(av: Double){
        average = av
    }

    fun getAverage(): Double{
        return Math.round(average*10.0)/10.0
    }
}
