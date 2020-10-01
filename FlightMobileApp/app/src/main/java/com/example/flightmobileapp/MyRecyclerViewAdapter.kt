package com.example.flightmobileapp
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

import com.example.flightmobileapp.databinding.ListItemBinding
import com.example.flightmobileapp.db.Server
//hold the list
class MyRecyclerViewAdapter(
    private val clickListener: (Server)->Unit): RecyclerView.Adapter<MyViewHolder>()
{
    private val subscribersList = ArrayList<Server>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding = DataBindingUtil.inflate(
            layoutInflater,R.layout.list_item,
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return subscribersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscribersList[position],clickListener)
    }

    fun setList(subscribers: List<Server>) {
        subscribersList.clear()
        subscribersList.addAll(subscribers)

    }

}

class MyViewHolder(val binding: ListItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bind(subscriber: Server,clickListener: (Server)->Unit){
        binding.nameTextView.text = subscriber.url
        binding.listItemLayout.setOnClickListener{
            clickListener(subscriber)
        }
    }
}