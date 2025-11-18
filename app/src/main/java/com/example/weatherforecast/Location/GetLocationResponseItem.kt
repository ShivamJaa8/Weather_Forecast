package com.example.weatherforecast.Location

data class GetLocationResponseItem(
    val addresstype: String,
    val boundingbox: List<String>,
    val `class`: String,
    var display_name: String,
    val importance: Double,
    var lat: String,
    val licence: String,
    var lon: String,
    val name: String,
    val osm_id: Long,
    val osm_type: String,
    val place_id: Int,
    val place_rank: Int,
    val type: String
)

object CurrentLocation {
    var location: GetLocationResponseItem? = null
}