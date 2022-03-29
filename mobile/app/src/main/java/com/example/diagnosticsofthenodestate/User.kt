package com.example.diagnosticsofthenodestate

import android.graphics.Color

data class User(var username:String,
                var green:String,
                var yellow:String,
                var red:String,
                var state: Int,
                var stateRYG: String,
                var timeSet: String
                )