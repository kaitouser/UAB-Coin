import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import uab.uab_coin.R

class HistoricAdapter(private val shopList: ArrayList<String>) :
    RecyclerView.Adapter<HistoricAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentLog = shopList[position]
        holder.tvboughtPrice.text = currentLog
    }

    override fun getItemCount(): Int {
        return shopList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvboughtPrice : TextView = itemView.findViewById(R.id.tvBoughtPrice)
    }
}