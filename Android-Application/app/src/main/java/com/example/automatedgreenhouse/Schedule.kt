package com.example.automatedgreenhouse

class Schedule {
    private var scheduleMap = mutableMapOf<Int, String>() //index, date in entryList
    private var entryList = mutableListOf<String>() //store time and entry in successions
    private var entryCount = 0

    fun update(date: String, time:String, entry:String){
        scheduleMap[entryCount] = date

        entryList.add(entryCount, time)
        entryCount+=1
        entryList.add(entryCount, entry)
        entryCount+=1
    }

    fun display(){
        println("Here is what your current schedule shows")
        if (entryCount>0) {
            for (i in 0..entryCount step 2) {
                println()
                if (i < entryCount) {
                    var date = scheduleMap[i]
                    var time = entryList[i]
                    var entry = entryList[i + 1]
                    println("On $date you have $entry at $time")
                }
            }
        }
    }

    init {
        //Temporary initialization statement for testing
        print("You created a new Schedule")
        println()
    }
}

fun main() {
    val testSchedule = Schedule()
    testSchedule.display() //expected
    testSchedule.update("11/30/2021", "6:30", "Irrigation off")
    testSchedule.display() //expected
    testSchedule.update("12/2/2021", "9:20", "Lights off")
    testSchedule.display() //problem, above update didn't work right
    testSchedule.update("12/2/2021", "13:10", "Lights on")
    testSchedule.display()
    testSchedule.update("12/3/2021", "15:25", "Lights on")
    testSchedule.display()
}
