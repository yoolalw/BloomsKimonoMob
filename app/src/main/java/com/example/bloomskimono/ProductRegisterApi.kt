package com.example.bloomskimono

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ProductRegisterApi {

    @Multipart
    @POST("products") // SEM BARRA NA FRENTE
    fun registerProduct(
        @Part("nomeKimono") nomeKimono: RequestBody,
        @Part("precoKimono") precoKimono: RequestBody,
        @Part("quantidadeKimono") quantidadeKimono: RequestBody,
        @Part imagem: MultipartBody.Part
    ): Call<ResponseBody>
}
