package com.mayank.superapp.issues

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mayank.superapp.RetrofitClient
import com.mayank.superapp.databinding.ActivityAddIssueBinding
import com.mayank.superapp.issues.IssueReportRequest
import kotlinx.coroutines.launch

/** Activity for creating a new issue */
class AddIssueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIssueBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSpinners()

        binding.btnSubmit.setOnClickListener {
            submitIssue()
        }
    }

    private fun setupSpinners() {
        binding.spinnerCategory.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            IssueCategory.values().map { it.name.replace('_', ' ') }
        )

        binding.spinnerPriority.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            Priority.values().map { it.name }
        )

        val handlers = listOf("MP", "MLA", "DM")
        binding.spinnerHandler.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            handlers
        )
    }

    private fun submitIssue() {
        val title = binding.etTitle.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        if (title.isBlank() || description.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val roads = binding.spinnerCategory.selectedItem.toString()
        val low = binding.spinnerPriority.selectedItem.toString()
        val mp = binding.spinnerHandler.selectedItem.toString()

        val request = IssueReportRequest(
            title = title,
            description = description,
            roads = roads,
            low = low,
            mp = mp,
            image = ""
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.reportService.submitReport(request)
                if (response.isSuccessful) {
                    val message = response.body()?.message ?: "Issue submitted"
                    Toast.makeText(this@AddIssueActivity, message, Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@AddIssueActivity, "Failed: ${'$'}{response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AddIssueActivity, "Error: ${'$'}{e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
