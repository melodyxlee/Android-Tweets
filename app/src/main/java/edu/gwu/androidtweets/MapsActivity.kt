package edu.gwu.androidtweets

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.widget.Button

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.jetbrains.anko.doAsync

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var confirm: Button

    private var currentAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        confirm = findViewById(R.id.confirm)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        confirm.setOnClickListener {
            if (currentAddress != null) {
                val intent = Intent(this, TweetsActivity::class.java)
                intent.putExtra("location", currentAddress)
                startActivity(intent)
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        mMap.setOnMapClickListener { coordinates ->

            googleMap.clear()

            doAsync {
                // Code in here runs in the background
                val geocoder = Geocoder(this@MapsActivity)
                var results: List<Address> = geocoder.getFromLocation(
                    coordinates.latitude,
                    coordinates.longitude,
                    5
                )

                // Need to check that results array actually has elements
                val first: Address = results[0]
                currentAddress = first
                val buttonTitle = first.getAddressLine(0)

                // UI can only be updated from the UI Thread
                // (so we need to switch back)
                runOnUiThread{
                    // Update the UI
                    confirm.text = buttonTitle

                    mMap.addMarker(
                        MarkerOptions().position(coordinates)
                    )

                    updateConfirmButton(first)
                }
            }

//            // This code executes whenever the user long-presses
//            val latLng = LatLng(38.898365, -77.046753)
//            val title = "GWU"
//            googleMap.addMarker(
//                MarkerOptions().position(latLng).title(title)
//            )
//            val zoomLevel = 5.0f
//            // You can also use moveCamera if you don't want the animation
//            googleMap.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel)
//            )
        }
    }

    private fun updateConfirmButton(address: Address) {
        // Update the button color -- need to load the color from resources first
        val greenColor = ContextCompat.getColor(
            this, R.color.button_green
        )
        val checkIcon = ContextCompat.getDrawable(
            this, R.drawable.ic_check_clear_24dp
        )
        confirm.setBackgroundColor(greenColor)

        // Update the left-aligned icon
        confirm.setCompoundDrawablesWithIntrinsicBounds(checkIcon, null, null, null)

        //Update button text
        confirm.text = address.getAddressLine(0)
        confirm.isEnabled = true
    }
}
