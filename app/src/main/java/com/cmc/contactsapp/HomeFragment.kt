package com.cmc.contactsapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.cmc.contactsapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var contactsAdapter: ContactsAdapter
    private var contactList: List<Contact> = listOf()
    private var filteredContactList: List<Contact> = listOf()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val permissionsRequestCode = 1001
    private val callPermissionRequestCode = 1002

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialiser l'adaptateur et le RecyclerView
        contactsAdapter = ContactsAdapter(filteredContactList, requireContext()) { contact ->
            onContactClicked(contact)
        }
        binding.recyclerViewContacts.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewContacts.adapter = contactsAdapter

        // Vérifier la permission d'accès aux contacts
        if (checkPermissions()) {
            // Si la permission est déjà accordée, charger les contacts immédiatement
            loadContacts()
        } else {
            // Sinon, demander la permission
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), permissionsRequestCode)
        }

        return binding.root
    }

    private fun checkPermissions(): Boolean {
        // Vérifie si la permission d'accéder aux contacts est accordée
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("Range")
    private fun loadContacts() {
        val contacts = mutableListOf<Contact>()

        // Contenu du resolver pour accéder aux contacts
        val contentResolver: ContentResolver = requireContext().contentResolver

        // Query pour récupérer les contacts
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null, null
        )

        cursor?.let {
            if (it.moveToFirst()) {
                do {
                    val name = it.getString(
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                    )
                    val phoneNumber = it.getString(
                        it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    )

                    // Ajouter chaque contact à la liste
                    contacts.add(Contact(name, phoneNumber))
                } while (it.moveToNext())
            }
            it.close()
        }

        // Mettre à jour la liste des contacts dans l'adaptateur
        contactList = contacts
        filteredContactList = contacts
        contactsAdapter.updateContacts(filteredContactList)
    }

    fun filterContacts(query: String?) {
        val queryLower = query?.lowercase() ?: ""
        filteredContactList = contactList.filter { contact ->
            contact.nomContact.lowercase().contains(queryLower) || contact.numeroContact.lowercase()
                .contains(queryLower)
        }
        contactsAdapter.submitList(filteredContactList) // Met à jour la liste dans l'adaptateur
    }

    // Gérer la réponse aux demandes de permission
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == permissionsRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission accordée, charger les contacts
                loadContacts()
            } else {
                // Permission refusée, afficher un message
                Toast.makeText(
                    requireContext(),
                    "Permission d'accès aux contacts refusée",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == callPermissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission d'appel accordée, récupérer le numéro et passer l'appel
                val prefs = requireContext().getSharedPreferences(
                    "app_prefs",
                    android.content.Context.MODE_PRIVATE
                )
                val phoneNumber = prefs.getString("phoneNumber", null)
                phoneNumber?.let {
                    val intent = Intent(Intent.ACTION_CALL)
                    intent.data = Uri.parse("tel:$it")
                    startActivity(intent)
                }
            } else {
                // Permission d'appel refusée, afficher un message Toast
                Toast.makeText(requireContext(), "Permission d'appel refusée.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    // Fonction pour gérer le clic sur un contact
    private fun onContactClicked(contact: Contact) {
        val options = arrayOf("Appeler", "Envoyer un message")
        val builder = android.app.AlertDialog.Builder(requireContext())
        builder.setTitle("Que voulez-vous faire ${contact.nomContact}?")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // Si "Appeler" est sélectionné, appeler makeCall
                    makeCall(contact.numeroContact)
                }

                1 -> {
                    // Si "Envoyer un message" est sélectionné
                    sendMessage(contact.numeroContact)
                }
            }
        }
        builder.show()
    }

    // Fonction pour passer un appel
    private fun makeCall(phoneNumber: String) {
        // Stocker le numéro de téléphone dans SharedPreferences pour y accéder après la demande de permission
        val prefs =
            requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString("phoneNumber", phoneNumber)
        editor.apply()

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission déjà accordée, passer l'appel
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")
            startActivity(intent)
        } else {
            // Demander la permission si elle n'est pas encore accordée
            requestPermissions(
                arrayOf(Manifest.permission.CALL_PHONE),
                callPermissionRequestCode // Code de la requête de permission
            )
        }
    }

    // Fonction pour envoyer un SMS
    private fun sendMessage(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
        intent.putExtra("sms_body", "") // Message par défaut
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nettoyage pour éviter les fuites de mémoire
    }
}
