package com.cmc.contactsapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.cmc.contactsapp.databinding.ItemContactBinding

class ContactsAdapter(
    private var contactList: List<Contact>,
    private val context: Context,
    private val onContactClicked: (Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

    // ViewHolder pour un contact
    class ContactViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemContactBinding.bind(view)
        val initialTextView: TextView = binding.contactInitial
        val bgContactInitial: CardView = binding.bgContactInitial
        val nomContact: TextView = binding.nomContact
        val numContact: TextView = binding.numeroContact
    }

    // Création du ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false)
        return ContactViewHolder(view)
    }

    // Lier les données aux vues
    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = contactList[position]

        // Obtenir la première lettre du nom du contact pour l'initiale
        val initial = contact.nomContact.firstOrNull()?.toString()?.uppercase() ?: "?"

        // Appliquer l'initiale dans le TextView
        holder.initialTextView.text = initial

        // Appliquer la couleur de fond du cercle en fonction de la première lettre
        val color = getColorForCercle(initial.first())
        holder.bgContactInitial.setCardBackgroundColor(
            ContextCompat.getColor(
                holder.itemView.context,
                color
            )
        )

        // Remplir les autres données : nom et numéro de téléphone
        holder.nomContact.text = contact.nomContact
        holder.numContact.text = contact.numeroContact

        // Gérer le clic sur un contact
        holder.itemView.setOnClickListener {
            onContactClicked(contact)
        }
    }

    // Nombre total d'éléments dans la liste
    override fun getItemCount(): Int = contactList.size

    // Fonction pour mettre à jour la liste des contacts
    fun updateContacts(newContactList: List<Contact>) {
        contactList = newContactList
        notifyDataSetChanged()
    }

    fun submitList(newContactList: List<Contact>) {
        contactList = newContactList
        notifyDataSetChanged() // Notifie l'adaptateur d'un changement dans la liste
    }

    // Fonction pour obtenir la couleur de fond du cercle basée sur la lettre
    private fun getColorForCercle(letter: Char): Int {
        return when (letter) {
            'A' -> android.R.color.holo_red_dark
            'B' -> android.R.color.holo_orange_dark
            'C' -> android.R.color.holo_green_dark
            'D' -> android.R.color.holo_blue_dark
            'E' -> android.R.color.holo_purple
            'F' -> android.R.color.holo_blue_bright
            'G' -> android.R.color.darker_gray
            'H' -> android.R.color.holo_red_light
            'I' -> android.R.color.holo_orange_light
            'J' -> android.R.color.holo_green_light
            'K' -> android.R.color.holo_purple
            'L' -> android.R.color.holo_blue_light
            'M' -> android.R.color.holo_red_dark
            'N' -> android.R.color.holo_orange_dark
            'O' -> android.R.color.holo_green_dark
            'P' -> android.R.color.holo_blue_dark
            'Q' -> android.R.color.holo_purple
            'R' -> android.R.color.holo_blue_bright
            'S' -> android.R.color.darker_gray
            'T' -> android.R.color.holo_red_light
            'U' -> android.R.color.holo_orange_light
            'V' -> android.R.color.holo_green_light
            'W' -> android.R.color.holo_purple
            'X' -> android.R.color.holo_blue_light
            'Y' -> android.R.color.holo_red_dark
            'Z' -> android.R.color.holo_orange_dark
            else -> android.R.color.darker_gray // Par défaut pour les caractères non définis
        }
    }

    // Afficher une boîte de dialogue avec les options "Appeler" ou "Envoyer un message"
    private fun showContactOptionsDialog(contact: Contact) {
        val options = arrayOf("Appeler", "Envoyer un message")

        // Créer un AlertDialog pour choisir l'action
        val builder = android.app.AlertDialog.Builder(context)
        builder.setTitle("Que voulez-vous faire ?")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> {
                    // Si "Appeler" est sélectionné
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

    // Passer un appel
    private fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phoneNumber")
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CALL_PHONE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            context.startActivity(intent)
        } else {
            // Demander la permission d'appel si elle n'est pas accordée
            if (context is android.app.Activity) {
                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    1002
                )
            }
        }
    }

    // Envoyer un message SMS
    private fun sendMessage(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:$phoneNumber"))
        intent.putExtra("sms_body", "") // Message par défaut
        context.startActivity(intent)
    }

}
