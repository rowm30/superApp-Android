package com.mayank.superapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.mayank.superapp.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var preferencesHelper: PreferencesHelper

    companion object {
        private const val TAG = "LoginActivity"
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Sign-in result received. Result code: ${result.resultCode}")

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            Log.d(TAG, "Google Sign-In successful")
            Log.d(TAG, "Account Email: ${account?.email}")
            Log.d(TAG, "Account Display Name: ${account?.displayName}")
            Log.d(TAG, "ID Token present: ${account?.idToken != null}")
            Log.d(TAG, "ID Token length: ${account?.idToken?.length ?: 0}")

            account?.idToken?.let { token ->
                Log.d(TAG, "ID Token (first 50 chars): ${token.take(50)}...")
                sendTokenToBackend(token)
            } ?: run {
                Log.e(TAG, "ID Token is null!")
                Toast.makeText(this, "Failed to get ID token", Toast.LENGTH_SHORT).show()
            }
        } catch (e: ApiException) {
            Log.e(TAG, "Google sign in failed with status code: ${e.statusCode}")
            Log.e(TAG, "Error message: ${e.message}")
            Log.e(TAG, "Error: ", e)

            val errorMessage = when (e.statusCode) {
                12501 -> "Sign-in cancelled"
                12500 -> "Sign-in failed - Configuration error"
                7 -> "Network error"
                else -> "Sign-in failed: ${e.message}"
            }
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        // IMPORTANT: Replace with your actual Web Client ID
        val webClientId = "135755435543-h985hivmurvo17rrkatsat61i5cpi207.apps.googleusercontent.com"
        Log.d(TAG, "Configuring Google Sign-In with Web Client ID: $webClientId")

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(webClientId)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnContinueGoogle.setOnClickListener {
            Log.d(TAG, "Sign-in button clicked")
            signIn()
        }
    }

    private fun signIn() {
        Log.d(TAG, "Starting sign-in process")
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    private fun sendTokenToBackend(idToken: String) {
        Log.d(TAG, "Sending token to backend")
        Log.d(TAG, "Backend URL: ${RetrofitClient.BASE_URL}")

        binding.progressBar.visibility = View.VISIBLE
        binding.btnContinueGoogle.visibility = View.GONE

        lifecycleScope.launch {
            try {
                Log.d(TAG, "Making API call to backend")
                val request = GoogleSignInRequest(idToken)
                Log.d(TAG, "Request created with token")

                val response = RetrofitClient.authService.googleSignIn(request)

                Log.d(TAG, "Response received - Success: ${response.isSuccessful}")
                Log.d(TAG, "Response code: ${response.code()}")
                Log.d(TAG, "Response message: ${response.message()}")

                if (response.isSuccessful) {
                    response.body()?.data?.let { jwt ->
                        Log.d(TAG, "Auth OK – User: ${jwt.user.email}")
                        preferencesHelper.saveAuthToken(jwt.accessToken)
                        navigateToMain()
                    } ?: run {
                        Toast.makeText(this@LoginActivity, "Empty payload", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Authentication failed - Error body: $errorBody")
                    Toast.makeText(this@LoginActivity, "Authentication failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network error: ${e.message}")
                Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
                Log.e(TAG, "Full error: ", e)
                Toast.makeText(this@LoginActivity, "Network error: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                binding.progressBar.visibility = View.GONE
                binding.btnContinueGoogle.visibility = View.VISIBLE
            }
        }
    }

    private fun navigateToMain() {
        Log.d(TAG, "Navigating to MainActivity")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
