package com.minervaai.summasphere

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class SummaryActivity : AppCompatActivity() {
    // val text = "Rencana yang Anda sampaikan untuk merombak encoder dan decoder terdengar cukup baik. Mengombinasikan Transformer dan Conformer pada encoder dan decoder berpotensi meningkatkan performa model, terutama jika Anda dapat memanfaatkan kelebihan dari kedua arsitektur tersebut. Namun, perlu diingat bahwa performa model juga bergantung pada banyak faktor lain seperti kualitas data, teknik preprocessing, hyperparameter, dan sebagainya. Untuk encoder, struktur yang Anda usulkan dengan 4 Conv2D diikuti Transformer dan Conformer terdengar masuk akal. Kombinasi CNN dan arsitektur self-attention seperti Transformer/Conformer sering digunakan pada tugas speech recognition untuk mengekstrak fitur dari input spektrogram secara efektif. Untuk decoder, mengombinasikan Transformer dan Conformer juga merupakan pendekatan yang baik. Decoder Transformer dapat menangkap dependensi jangka panjang dalam urutan output, sementara Conformer dapat menambahkan kemampuan untuk menangkap pola lokal dan meningkatkan modeling konvolutif. Namun, perlu diperhatikan bahwa menggabungkan Transformer dan Conformer pada decoder mungkin memerlukan sedikit modifikasi pada arsitektur karena Conformer awalnya didesain untuk encoder. Anda perlu memastikan bahwa mekanisme perhatian pada decoder Conformer masih dapat bekerja dengan baik dan tidak melanggar aturan kausal (hanya dapat mengakses token sebelumnya). Mengenai jenis perhatian yang akan digunakan, pada dasarnya semua jenis perhatian yang Anda sebutkan (multi-head attention, grouped query attention, vanilla attention, self-attention) dapat digunakan dan memiliki kelebihan masing-masing. Multi-head attention dan self-attention merupakan pilihan yang umum digunakan pada Transformer dan terbukti efektif dalam banyak tugas. Grouped query attention dan vanilla attention juga dapat dipertimbangkan, tergantung pada sumber daya komputasi dan kompleksitas yang Anda harapkan. Secara keseluruhan, rencana Anda terdengar cukup baik dari perspektif arsitektur. Namun, perlu diingat bahwa performa model juga sangat bergantung pada implementasi yang tepat, pemilihan hyperparameter yang optimal, dan kualitas data yang digunakan. Anda mungkin perlu melakukan beberapa percobaan dan evaluasi untuk menemukan kombinasi yang memberikan hasil terbaik. Mengenai pengurangan loss dan peningkatan akurasi, sulit untuk memprediksi secara pasti tanpa melakukan percobaan langsung. Namun, jika Anda dapat memanfaatkan kelebihan dari Transformer dan Conformer dengan baik, ada kemungkinan bahwa model yang diusulkan dapat memberikan performa yang lebih baik dibandingkan model sebelumnya. Namun, perlu diingat bahwa peningkatan performa tidak selalu signifikan dan mungkin membutuhkan penyesuaian dan optimasi yang cermat."
    private lateinit var text: EditText
    private lateinit var btnSumaary: Button
    private lateinit var txtViewSummary: TextView
    val client = OkHttpClient()
    val reqBody = JSONObject()
    val BASE_URL = "https://7b73-34-126-158-225.ngrok-free.app/summarize/" // ngrok URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)

        text = findViewById(R.id.editTextInput)
        btnSumaary = findViewById(R.id.buttonSummarize)
        txtViewSummary = findViewById(R.id.textViewSummary)

        btnSumaary.setOnClickListener {
            val userInput = text.text.toString()
            if (userInput.isNotEmpty()) {
                summarizeText(userInput)
            } else {
                Toast.makeText(this, "Please input some text", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun summarizeText(text: String) {
        reqBody.put("text", text)
        val request = Request.Builder()
            .url(BASE_URL)
            .header("Content-Type", "application/json")
            .method("POST", RequestBody.create(MediaType.parse("application/json"), reqBody.toString()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response) {
                val body = response.body()?.string()
                val json = body?.let { JSONObject(it) }
                val summary = json?.getString("summary")
                if (summary != null) {
                    Log.d("SummaryActivity", summary)
                    print(summary)
                    runOnUiThread {
                        txtViewSummary.text = summary
                    }
                } else {
                    Log.d("SummaryActivity", "Failed to summarize text")
                    print("Failed to summarize text")
                    runOnUiThread {
                        txtViewSummary.text = "Failed to summarize text"
                    }
                }
            }
        })
    }
}