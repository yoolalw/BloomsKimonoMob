package com.example.bloomskimono.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bloomskimono.ApiConfig
import com.example.bloomskimono.ProductApi
import com.example.bloomskimono.adapter.ProductAdapter
import com.example.bloomskimono.models.Product
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.bloomskimono.R
import android.content.Intent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.example.bloomskimono.ProductRegisterActivity


class MainActivity : AppCompatActivity() {

    private val api = ApiConfig.retrofit.create(ProductApi::class.java)
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnAddProduto = findViewById<FloatingActionButton>(R.id.btnAddProduto)

        btnAddProduto.setOnClickListener {
            val intent = Intent(this, ProductRegisterActivity::class.java)
            startActivity(intent)
        }

        // Inicialização do RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Requisição para pegar a lista de produtos
        api.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val produtos = response.body() ?: emptyList()
                    adapter = ProductAdapter(produtos)
                    recyclerView.adapter = adapter
                } else {
                    Log.e("MainActivity", "Erro ao carregar produtos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("MainActivity", "Falha na requisição: ${t.message}")
            }
        })
    }
}
