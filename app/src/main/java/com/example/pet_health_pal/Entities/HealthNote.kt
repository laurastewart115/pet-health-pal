package com.example.pet_health_pal.Entities

import org.threeten.bp.LocalDate


class HealthNote(val id: Int,
                 val Date: LocalDate,
                 val Title: String,
                 val NoteText: String,
                 var pet_id : Int) {
}