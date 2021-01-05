package com.example.pet_health_pal.Entities

import org.threeten.bp.LocalTime

class RecurringReminder(
    val id: Int,
    var Title: String,
    var Weekday: String,
    var Active: Boolean,
    var Time: LocalTime,
    var pet_id : Int) {}