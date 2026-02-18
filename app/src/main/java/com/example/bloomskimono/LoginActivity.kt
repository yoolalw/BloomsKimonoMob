package com.example.bloomskimono

import com.example.bloomskimono.ui.MainActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// 1. Campos alterados para bater com o Java (UserModel)
data class LoginRequest(
    val nomeUser: String,
    val emailUser: String,
    val senhaUser: String
)

// O Java retorna uma lista de usuários após o cadastro
interface ApiService {
    @POST("/users/register")
    fun register(@Body request: LoginRequest): Call<List<Map<String, Any>>>
}

class LoginActivity : AppCompatActivity() {

    private val apiService: ApiService by lazy {
        ApiConfig.retrofit.create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val email = edtEmail.text.toString()
            val senha = edtSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enviando com os nomes que o seu Java entende
            val request = LoginRequest(
                nomeUser = "Usuario Android", // Nome padrão pois seu Java exige
                emailUser = email,
                senhaUser = senha
            )

            apiService.register(request).enqueue(object : Callback<List<Map<String, Any>>> {
                override fun onResponse(
                    call: Call<List<Map<String, Any>>>,
                    response: Response<List<Map<String, Any>>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Sucesso no servidor!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Erro: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Map<String, Any>>>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Falha na conexão ❌", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}