package uab.uab_coin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uab.uab_coin.R
import uab.uab_coin.models.OfferModel

class OfferAdapter(private val offerList: ArrayList<OfferModel>) :
    RecyclerView.Adapter<OfferAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.offers_list_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentOffer = offerList[position]
        holder.tvOfferName.text = currentOffer.offerName
        holder.tvOfferPrice.text = currentOffer.offerPrice.toString()
    }

    override fun getItemCount(): Int {
        return offerList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvOfferName : TextView = itemView.findViewById(R.id.tvOfferName)
        val tvOfferPrice : TextView = itemView.findViewById(R.id.tvOfferPrice)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }

    }
}