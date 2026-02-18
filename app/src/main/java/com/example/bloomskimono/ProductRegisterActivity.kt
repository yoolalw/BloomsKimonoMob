package com.example.bloomskimono

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class ProductRegisterActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 100
    private var imageFile: File? = null

    private val api: ProductRegisterApi by lazy {
        ApiConfig.retrofit.create(ProductRegisterApi::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_productregister)

        val toolbar = findViewById<Toolbar>(R.id.toolbarProduto)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Cadastrar Produto"

        val edtNome = findViewById<EditText>(R.id.edtNomeKimono)
        val edtPreco = findViewById<EditText>(R.id.edtPrecoKimono)
        val edtQtd = findViewById<EditText>(R.id.edtQtdKimono)
        val edtImagem = findViewById<EditText>(R.id.edtImagemKimono)
        val btnSelecionar = findViewById<Button>(R.id.btnSelecionarImagem)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrarProduto)

        btnSelecionar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Selecione uma imagem"), PICK_IMAGE_REQUEST)
        }

        btnCadastrar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val precoTxt = edtPreco.text.toString().replace(",", ".").trim()
            val qtdTxt = edtQtd.text.toString().trim()

            if (nome.isEmpty() || precoTxt.isEmpty() || qtdTxt.isEmpty() || imageFile == null) {
                Toast.makeText(this, "Preencha todos os campos e selecione uma imagem!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val preco = precoTxt.toDoubleOrNull()
            val qtd = qtdTxt.toIntOrNull()

            if (preco == null || qtd == null) {
                Toast.makeText(this, "Verifique os valores de preço e quantidade!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nomeBody = nome.toRequestBody("text/plain".toMediaTypeOrNull())
            val precoBody = precoTxt.toRequestBody("text/plain".toMediaTypeOrNull())
            val qtdBody = qtdTxt.toRequestBody("text/plain".toMediaTypeOrNull())

            val requestFile = imageFile!!.asRequestBody("image/*".toMediaTypeOrNull())
            val imagemPart = MultipartBody.Part.createFormData(
                "imagem",
                imageFile!!.name,
                requestFile
            )

            api.registerProduct(nomeBody, precoBody, qtdBody, imagemPart)
                .enqueue(object : Callback<ResponseBody> {

                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@ProductRegisterActivity,
                                "Produto cadastrado com sucesso! ✅",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        } else {
                            Toast.makeText(
                                this@ProductRegisterActivity,
                                "Erro do servidor: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            this@ProductRegisterActivity,
                            "Falha na conexão: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST &&
            resultCode == Activity.RESULT_OK &&
            data?.data != null
        ) {
            val uri = data.data!!
            imageFile = uriToFile(uri)
            findViewById<EditText>(R.id.edtImagemKimono)
                .setText(getFileName(uri))
        }
    }

    private fun uriToFile(uri: Uri): File? {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(cacheDir, getFileName(uri))
        val outputStream = FileOutputStream(file)

        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()

        return file
    }

    private fun getFileName(uri: Uri): String {
        var name = "imagem.jpg"

        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
