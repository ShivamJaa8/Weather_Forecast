package com.example.weatherforecast.Adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherforecast.Activities.MainActivity
import com.example.weatherforecast.Location.CurrentLocation
import com.example.weatherforecast.Location.GetLocationResponseItem
import com.example.weatherforecast.databinding.ItemContainerLocationBinding

class LocationAdapter(val context:Context) : RecyclerView.Adapter<LocationAdapter.ViewHolder>() {

    private var locations = mutableListOf<GetLocationResponseItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LocationAdapter.ViewHolder {
        val binding = ItemContainerLocationBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LocationAdapter.ViewHolder, position: Int) {
        holder.bind(locations.get(position))


    }

    override fun getItemCount(): Int {
        return locations.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun RefreshData(location: List<GetLocationResponseItem>){
        locations.clear()
        locations.addAll(location)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemContainerLocationBinding)
        : RecyclerView.ViewHolder(binding.root){
        fun bind(data: GetLocationResponseItem){
            with(binding){
                textRemoteLocation.text=data.display_name
            }
            itemView.setOnClickListener {
                CurrentLocation.location=data
                itemView.context.startActivity(Intent(itemView.context, MainActivity::class.java))
//                if(context is Activity) {
//                    context.finish()
//                }
            }
        }
    }

}