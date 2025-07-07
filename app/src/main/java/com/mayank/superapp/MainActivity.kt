package com.mayank.superapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mayank.superapp.RetrofitClient
import com.mayank.superapp.SittingMember
import com.mayank.superapp.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import android.graphics.drawable.GradientDrawable
import android.graphics.Color

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        preferencesHelper = PreferencesHelper(this)
        RetrofitClient.setPreferencesHelper(preferencesHelper)

        // Check if user is logged in
        if (!preferencesHelper.isLoggedIn()) {
            navigateToLogin()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure Google Sign-In for logout
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Setup bottom navigation
        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    binding.scrollContent.visibility = View.GONE
                    findViewById<View>(R.id.profileTab).visibility = View.VISIBLE
                    loadUserProfile()
                    true
                }
                else -> {
                    findViewById<View>(R.id.profileTab).visibility = View.GONE
                    binding.scrollContent.visibility = View.VISIBLE
                    true
                }
            }
        }
        binding.bottomNav.selectedItemId = R.id.nav_officials

        // Setup logout button in profile tab
        findViewById<View>(R.id.profileTab)?.findViewById<Button>(R.id.btnLogout)?.setOnClickListener {
            logout()
        }

        // Request location permission
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (granted) {
            getLocation()
        } else {
            Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLatitude = location.latitude
                currentLongitude = location.longitude

                // Update location display in the header
                updateLocationDisplay()

                // Fetch constituency and representatives
                fetchConstituencyAndRepresentatives()
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateLocationDisplay() {
        // Update the location TextViews in the header card
        findViewById<TextView>(R.id.tvLocationArea)?.text = "Detecting location..."
        findViewById<TextView>(R.id.tvLocationConstituency)?.text = "Please wait..."
    }

    private fun fetchConstituencyAndRepresentatives() {
        lifecycleScope.launch {
            try {
                // First, get the constituency name based on location
                val constituencyList = RetrofitClient.api.getConstituencyByLocation(
                    currentLatitude,
                    currentLongitude
                )

                if (constituencyList.isNotEmpty()) {
                    val constituency = constituencyList[0]

                    // Update location display with constituency name
                    updateConstituencyDisplay(constituency)

                    // Fetch sitting members for this constituency
                    val sittingMembers = RetrofitClient.api.getSittingMembers(constituency)

                    // Update UI with representatives
                    displayRepresentatives(sittingMembers)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No constituency found for your location",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "Error fetching data: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun updateConstituencyDisplay(constituency: String) {
        runOnUiThread {
            // Update the location area name
            findViewById<TextView>(R.id.tvLocationArea)?.text = constituency

            // Update the state/district info
            findViewById<TextView>(R.id.tvLocationConstituency)?.text = "Parliamentary Constituency"

            // Remove the tvLocation TextView if it exists (it was in the wrong place in your layout)
//            findViewById<TextView>(R.id.tvLocation)?.visibility = View.GONE
        }
    }

    private fun displayRepresentatives(members: List<SittingMember>) {
        runOnUiThread {
            // Find the container where representatives are displayed
            val container = findViewById<LinearLayout>(R.id.representativesContainer)

            // Clear existing cards
            container?.removeAllViews()

            // Add a card for each representative
            members.forEach { member ->
                val card = createRepresentativeCard(member)
                container?.addView(card)
            }

            // If no representatives found
            if (members.isEmpty()) {
                val emptyView = TextView(this).apply {
                    text = "No representatives found for your location"
                    setPadding(16, 16, 16, 16)
                    setTextColor(Color.GRAY)
                }
                container?.addView(emptyView)
            }
        }
    }

    private fun createRepresentativeCard(member: SittingMember): CardView {
        val cardView = CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 16f
            cardElevation = 3f
        }

        // Create the card content
        val cardContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        // Header row
        val headerRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        // Badge
        val badge = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(48, 48)
            background = createCircleDrawable("#7B68EE") // Purple color
            gravity = android.view.Gravity.CENTER
            text = "MP"
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        // Info block
        val infoBlock = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            orientation = LinearLayout.VERTICAL
            setPadding(12, 0, 0, 0)
        }

        // Name
        val nameText = TextView(this).apply {
            text = member.nameOfMember
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(Color.parseColor("#212121"))
        }

        // Designation
        val designationText = TextView(this).apply {
            text = "Member of Parliament - ${member.constituency}"
            textSize = 12f
            setTextColor(Color.parseColor("#616161"))
        }

        // Party and Status
        val partyStatusText = TextView(this).apply {
            text = "• ${member.partyName} • ${member.membershipStatus}"
            textSize = 12f
            setTextColor(Color.parseColor("#00A043"))
        }

        infoBlock.addView(nameText)
        infoBlock.addView(designationText)
        infoBlock.addView(partyStatusText)

        headerRow.addView(badge)
        headerRow.addView(infoBlock)

        // Divider
        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply {
                setMargins(0, 12, 0, 0)
            }
            setBackgroundColor(Color.parseColor("#1F000000"))
        }

        // Meta row
        val metaRow = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 12, 0, 0)
            }
            orientation = LinearLayout.HORIZONTAL
            weightSum = 3f
        }

        // Add meta items
        val terms = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = "${member.lokSabhaTerms}\nTerms"
            gravity = android.view.Gravity.CENTER
            textSize = 12f
            setTextColor(Color.parseColor("#FF6B35"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val state = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = "State\n${member.state}"
            gravity = android.view.Gravity.CENTER
            textSize = 12f
            setTextColor(Color.parseColor("#FF6B35"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val status = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            text = "Status\n${member.membershipStatus}"
            gravity = android.view.Gravity.CENTER
            textSize = 12f
            setTextColor(Color.parseColor("#FF6B35"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        metaRow.addView(terms)
        metaRow.addView(state)
        metaRow.addView(status)

        // Add all views to card
        cardContent.addView(headerRow)
        cardContent.addView(divider)
        cardContent.addView(metaRow)

        cardView.addView(cardContent)

        return cardView
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            try {
                val user = RetrofitClient.userService.getCurrentUser()
                findViewById<TextView>(R.id.tvName)?.text = user.name
                findViewById<TextView>(R.id.tvEmail)?.text = user.email
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createCircleDrawable(color: String): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(color))
        }
    }

    private fun logout() {
        // Clear local token
        preferencesHelper.clearAuthToken()

        // Sign out from Google
        googleSignInClient.signOut().addOnCompleteListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}