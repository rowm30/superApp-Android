package com.mayank.superapp.issues

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.mayank.superapp.databinding.ActivityAddIssueBinding

/** Activity for creating a new issue */
class AddIssueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddIssueBinding
    private var imageUri: Uri? = null
    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            binding.ivPreview.setImageURI(it)
            binding.ivPreview.visibility = View.VISIBLE
            binding.btnRemoveImage.visibility = View.VISIBLE
            binding.btnAttachImage.text = getString(R.string.change_image)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSpinners()

        binding.btnAttachImage.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnRemoveImage.setOnClickListener {
            imageUri = null
            binding.ivPreview.setImageDrawable(null)
            binding.ivPreview.visibility = View.GONE
            binding.btnRemoveImage.visibility = View.GONE
            binding.btnAttachImage.text = getString(R.string.attach_image)
        }

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
        val message = if (imageUri != null) {
            "Issue submitted with image"
        } else {
            "Issue submitted"
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
