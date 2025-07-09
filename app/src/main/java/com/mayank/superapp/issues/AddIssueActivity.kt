package com.mayank.superapp.issues

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mayank.superapp.databinding.ActivityAddIssueBinding

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
        Toast.makeText(this, "Issue submitted", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
