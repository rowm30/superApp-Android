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
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import android.graphics.drawable.GradientDrawable
import android.graphics.Color

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesHelper: PreferencesHelper
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val issuesFragment = com.mayank.superapp.issues.IssuesFragment()
    private val servicesFragment = com.mayank.superapp.services.ServicesFragment()

    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    // Sample representatives shown when API calls fail or return empty
    private val sampleMembers = listOf(
        SittingMember(
            id = 1,
            lastName = "Kumar",
            nameOfMember = "Arun Kumar",
            partyName = "Demo Party",
            constituency = "Model Town",
            state = "Delhi",
            membershipStatus = "In Office",
            lokSabhaTerms = 2,
            district = "New Delhi",
            latitude = null,
            longitude = null,
            designation = "MP"
        ),
        SittingMember(
            id = 2,
            lastName = "Sharma",
            nameOfMember = "Sunita Sharma",
            partyName = "Example Party",
            constituency = "Gomti Nagar",
            state = "Uttar Pradesh",
            membershipStatus = "In Office",
            lokSabhaTerms = 1,
            district = "Lucknow",
            latitude = null,
            longitude = null,
            designation = "MLA"
        ),
        SittingMember(
            id = 3,
            lastName = "Verma",
            nameOfMember = "Rakesh Verma",
            partyName = "Administration",
            constituency = "Kanpur",
            state = "Uttar Pradesh",
            membershipStatus = "Serving",
            lokSabhaTerms = 0,
            district = "Kanpur",
            latitude = null,
            longitude = null,
            designation = "DM"
        )
    )

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
                    showProfile()
                    true
                }
                R.id.nav_issues -> {
                    showFragment(issuesFragment)
                    true
                }
                R.id.nav_services -> {
                    showFragment(servicesFragment)
                    true
                }
                else -> {
                    showOfficials()
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

                    // Update UI with representatives or show samples if none
                    if (sittingMembers.isNotEmpty()) {
                        displayRepresentatives(sittingMembers)
                    } else {
                        displayRepresentatives(sampleMembers)
                    }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "No constituency found for your location",
                        Toast.LENGTH_SHORT
                    ).show()
                    displayRepresentatives(sampleMembers)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@MainActivity,
                    "Error fetching data: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                displayRepresentatives(sampleMembers)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // ensure the main tab is highlighted when returning from other screens
        binding.bottomNav.selectedItemId = R.id.nav_officials
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
        val inflater = LayoutInflater.from(this)
        val cardView = inflater.inflate(R.layout.view_rep_card, null) as CardView

        cardView.findViewById<TextView>(R.id.badge)?.text = member.designation ?: "MP"
        cardView.findViewById<TextView>(R.id.repName)?.text = member.nameOfMember

        val designationText = when (member.designation) {
            "MP" -> "Member of Parliament"
            "MLA" -> "Member of Legislative Assembly"
            "DM" -> "District Magistrate"
            else -> "Representative"
        }
        val repDesignation = "$designationText - ${member.constituency}"
        cardView.findViewById<TextView>(R.id.repDesignation)?.text = repDesignation
        cardView.findViewById<TextView>(R.id.repStatus)?.text = "• ${member.membershipStatus}"

        return cardView
    }

    private fun loadUserProfile() {
        findViewById<TextView>(R.id.tvEmail)?.text = preferencesHelper.getUserEmail()
        findViewById<TextView>(R.id.tvName)?.text = preferencesHelper.getUserName()

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

    private fun showProfile() {
        binding.fragmentContainer.visibility = View.GONE
        binding.scrollContent.visibility = View.GONE
        findViewById<View>(R.id.profileTab).visibility = View.VISIBLE
        loadUserProfile()
    }

    private fun showOfficials() {
        binding.fragmentContainer.visibility = View.GONE
        findViewById<View>(R.id.profileTab).visibility = View.GONE
        binding.scrollContent.visibility = View.VISIBLE
    }

    private fun showFragment(fragment: androidx.fragment.app.Fragment) {
        findViewById<View>(R.id.profileTab).visibility = View.GONE
        binding.scrollContent.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
