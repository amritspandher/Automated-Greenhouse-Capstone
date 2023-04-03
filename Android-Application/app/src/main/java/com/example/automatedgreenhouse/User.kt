package com.example.automatedgreenhouse

import android.app.Application

class User : Application() {

    companion object
    {
        var userId = ""
        var greenhouseId = ""

        var greenhouseId_1 = ""
        var greenhouseName_1 = ""
        var moduleId_1 = ""

        var greenhouseId_2 = ""
        var greenhouseName_2 = ""
        var moduleId_2 =""

        var greenhouseId_3 = ""
        var greenhouseName_3 = ""
        var moduleId_3 =""

        var rdsUrl = "https://eknqepv86f.execute-api.us-east-1.amazonaws.com/database/rds"
        var timestreamUrl = "https://eknqepv86f.execute-api.us-east-1.amazonaws.com/database/timestream"
        var iotControlUrl = "https://eknqepv86f.execute-api.us-east-1.amazonaws.com/iot/control"
    }

    override fun onCreate()
    {
        super.onCreate()
    }
}