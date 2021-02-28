package com.example.hw1

import android.app.Dialog
import android.content.Context
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter (val context: Context, val items: ArrayList<Reminder>) :
        RecyclerView.Adapter<ItemAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_custom_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val item = items.get(position)

        holder.tvItem.text = item.message

        if (position % 2 == 0){
            holder.cardViewItem.setBackgroundColor(ContextCompat.getColor(context, R.color.lightgray))
        }else{
            holder.cardViewItem.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }
        holder.ivEdit.setOnClickListener{ view ->
            if (context is MenuActivity){
                context.editReminderDialog(item)
            }
        }
        holder.ivDelete.setOnClickListener{ view ->
            if (context is MenuActivity){
                context.deleteReminder(item)
            }
        }


        holder.cardViewItem.setOnClickListener{ view ->
            if (context is MenuActivity){
                //Toast.makeText(context, "klikkasit muistutusta", Toast.LENGTH_SHORT).show()
                context.informationDialog(item)
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvItem = view.findViewById<TextView>(R.id.itemName)
        val cardViewItem = view.findViewById<CardView>(R.id.card_view_item)
        val ivEdit = view.findViewById<ImageView>(R.id.editLogo)
        val ivDelete = view.findViewById<ImageView>(R.id.deleteLogo)

    }
}

