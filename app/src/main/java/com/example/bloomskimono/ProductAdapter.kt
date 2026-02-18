package com.example.bloomskimono.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bloomskimono.R
import com.example.bloomskimono.models.Product

class ProductAdapter(private val produtos: List<Product>) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_produto, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val produto = produtos[position]
        holder.nome.text = produto.nomeKimono
        holder.preco.text = "R$ ${produto.precoKimono}"

        // Atualize o caminho correto da imagem, conforme seu servidor
        Glide.with(holder.itemView.context)
            .load("http://192.168.18.6:8081/products/imagem/${produto.imagem}") // Caminho da imagem
            .into(holder.imagem)
    }

    override fun getItemCount(): Int = produtos.size

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nome: TextView = itemView.findViewById(R.id.tvNomeProduto)
        val preco: TextView = itemView.findViewById(R.id.tvPrecoProduto)
        val imagem: ImageView = itemView.findViewById(R.id.ivProduto)
    }
}
