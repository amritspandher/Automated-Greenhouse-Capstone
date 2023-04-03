package com.example.gcs

class Greenhouse(
    var name: String
) {
    val greenhouse_id: Int = 0 //immutable
    // notes for SPECIAL fields for the Greenhouse class
    // Overall schedule data, with individual module schedules contained
    // Modules - sensors/hardware/etc
    // Statistics - detailed history stats from all modules
    // Notes - add/edit/remove notes

    var user_id: Int = 0
        get() = field
        set(value){
            field = value
        }

    //The below get and set methods will give you the most recent SAVED reading value
    var temperature: Double = 0.0
        get() = field
        set(value){
            field = value
        }

    var humidity: Double = 0.0
        get() = field
        set(value){
            field = value
        }

    var soil_moisture: Double = 0.0
        get() = field
        set(value){
            field = value
        }

    var light_intensity: Double = 0.0
        get() = field
        set(value){
            field = value
        }

    //Commented out methods below have unclear set up, fields subject to change

    //var module_id: Int = 0
    //	get() = field
    //	set(value){
    //        field = value
    //    }

    //var schedule_id: Int = 0
    //	get() = field
    //	set(value){
    //        field = value
    //    }

    //The below get functions will give you the most recent reading and update SAVED value
    //Idea is the corresponding sensor object of the sensor interface (to be built) will be-
    //passed in as a parameter depending on which get method is called.
    /*
    fun get_temperature(s: sensor){
        this.temperature = s.get_temperature()
        return s.get_temperature()
    }

    fun get_humidity(s: sensor){
        this.humidity = s.get_humidity()
        return s.get_humidity()
    }

    fun get_soil_moisture(s: sensor){
        this.soil_moisture = s.get_soil_moisture()
        return s.get_soil_moisture()
    }

    fun get_light_intensity(s: sensor){
        this.light_intensity = s.get_light_intensity()
        return s.get_light_intensity()
    }
    */

    init {
        //Temporary initialization statement for testing
        print("You created a new Greenhouse called " + this.name)
    }
}

fun main() {
    val test_house = Greenhouse("Amrit's System")
    print(test_house.temperature)
}
