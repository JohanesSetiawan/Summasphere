package com.minervaai.summasphere.ui.onboarding

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.minervaai.summasphere.R
import com.minervaai.summasphere.SummaryActivity
import com.minervaai.summasphere.databinding.ActivityOnboardingBinding
import kotlin.math.sign

class OnboardingActivity : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var binding: ActivityOnboardingBinding

    private lateinit var auth : FirebaseAuth
    private lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnSignup.setOnClickListener {
            finish() // belum di intent
        }
        binding.btnGoogle.setOnClickListener {
            googleSignIn()
        }
        binding.btnLogin.setOnClickListener {
            finish() // belum di intent
        }

        mViewPager = findViewById(R.id.onboarding_viewpager)
        mViewPager.adapter = OnboardingAdapter(this, this)
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()
        mViewPager.offscreenPageLimit = 1

    }

    private fun googleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            manageResults(task)
        } else {
            Log.e("GoogleSignIn", "Google sign in failed 1")
            Toast.makeText(this, "TOLONG ERRORRRRR.R.R..R.R.R..R.R", Toast.LENGTH_SHORT).show()
        }
    }

    private fun manageResults(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
            if (account != null) {
                val cred = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(cred).addOnCompleteListener { signInTask ->
                    if (signInTask.isSuccessful) {
                        Log.d("GoogleSignIn", "signInWithCredential:success")
                        Intent(this, SummaryActivity::class.java).also {
                            startActivity(it)
                        }
                        Toast.makeText(this, "Successfully signed in with Google.", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("GoogleSignIn", "signInWithCredential:failure", signInTask.exception)
                        Toast.makeText(this, "Google Sign-In failed, please try again.", Toast.LENGTH_SHORT).show()
                    }
                }
                Log.d("GoogleSignIn", "firebaseAuthWithGoogle:" + account.id)
            } else {
                Log.e("GoogleSignIn", "Google sign in failed 2", task.exception)
                Toast.makeText(this, "Google Sign-In failed, please try again.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Google sign in failed 3", e)
            Toast.makeText(this, "Google Sign-In failed, please try again.", Toast.LENGTH_SHORT).show()
        }
    }
}