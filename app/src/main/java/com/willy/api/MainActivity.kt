package com.willy.api

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.willy.api.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var users = listOf<User>()
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.getUsersBtn.setOnClickListener { fetchUsers() }
        binding.nextBtn.setOnClickListener { showNextUser() }
        binding.prevBtn.setOnClickListener { showPreviousUser() }
        binding.getProductsBtn.setOnClickListener { fetchProductAverage() }
    }

    private fun fetchUsers() {
        RetrofitClient.instance.getUsers().enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    users = response.body()?.users ?: emptyList()
                    currentIndex = 0
                    showUser()
                } else {
                    Toast.makeText(this@MainActivity, "Gagal memuat user", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal mengambil data user", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showUser() {
        if (users.isEmpty()) return
        val user = users[currentIndex]
        binding.userText.text = """
        ID: ${user.id}
        Nama: ${user.firstName} ${user.lastName}
        Universitas: ${user.university}
    """.trimIndent()

        binding.emailText.text = "Email: ${user.email ?: "-"}"
        binding.prevBtn.isEnabled = currentIndex > 0
        binding.nextBtn.isEnabled = currentIndex < users.size - 1
    }

    private fun showNextUser() {
        if (currentIndex < users.size - 1) {
            currentIndex++
            showUser()
        }
    }

    private fun showPreviousUser() {
        if (currentIndex > 0) {
            currentIndex--
            showUser()
        }
    }

    private fun fetchProductAverage() {
        RetrofitClient.instance.getProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    val products = response.body()?.products ?: emptyList()
                    val avg = products.map { it.price }.average()
                    val format = NumberFormat.getCurrencyInstance(Locale("in", "ID")).format(avg)
                    binding.avgPriceText.text = "Rata-rata: $format"
                } else {
                    Toast.makeText(this@MainActivity, "Gagal memuat produk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Gagal mengambil data produk", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
