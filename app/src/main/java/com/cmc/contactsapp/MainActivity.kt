package com.cmc.contactsapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawer_layout)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)


        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    // Inflate le menu avec l'option de recherche
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        // Récupérer l'item de recherche
        val searchItem = menu?.findItem(R.id.search)

        // Récupérer le SearchView et le configurer
        val searchView = searchItem?.actionView as? SearchView
        searchView?.setQueryHint("Rechercher...")

        // Définir un listener pour écouter les événements de texte
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Gérer la soumission de la recherche (ex. filtrer une liste de contacts)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Passer le texte à HomeFragment pour filtrer
                val fragment =
                    supportFragmentManager.findFragmentById(R.id.fragment_container) as? HomeFragment
                fragment?.filterContacts(newText)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    // Gérer les actions de menu (quand l'élément est sélectionné)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                // Ici vous pouvez effectuer des actions supplémentaires sur le clic
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    // Gérer les éléments de navigation (drawer)
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> replaceFragment(HomeFragment())
            R.id.nav_info -> replaceFragment(InfoFragment())
            R.id.nav_share -> replaceFragment(ShareFragment())
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Remplacer le fragment actuel par un autre
    private fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    // Gérer le bouton de retour dans le drawer
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
