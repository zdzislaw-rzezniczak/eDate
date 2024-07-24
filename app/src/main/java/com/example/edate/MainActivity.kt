package com.example.edate
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import android.util.Log

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyApp() {


    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var location by remember { mutableStateOf<Location?>(null) }
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val value = basicReadWrite("${location?.latitude} : ${location?.longitude}")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (locationPermissionState.status.isGranted) {
            Button(onClick = {
                getLastKnownLocation(fusedLocationClient) { loc ->
                    location = loc
                }
            }) {
                Text(text = "Get Location")
            }

            Spacer(modifier = Modifier.height(16.dp))

            location?.let {
                Text(text = "Latitude: ${it.latitude}")
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Longitude: ${it.longitude}")
                Text(text = "Value is: $value")
            } ?: Text("Press the button to fetch location.")
        } else {
            Column {
                Text("Location permission is required to fetch the location.")
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                    Text("Request Permission")
                }
            }
        }
    }
}

@Composable
fun basicReadWrite(message: String) : String?{
    // [START write_message]
    // Write a message to the database
    var value by remember { mutableStateOf<String?>("Heyy") }
    val database = Firebase.database
    val myRef = database.getReference("message")

    myRef.setValue(message)

    // [END write_message]

    // [START read_message]
    // Read from the database
    myRef.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.
            value = dataSnapshot.getValue<String>()
            Log.d(TAG, "Value is: $value")
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    })
    // [END read_message]
    return value
}

@SuppressLint("MissingPermission")
fun getLastKnownLocation(client: FusedLocationProviderClient, onResult: (Location?) -> Unit) {
    client.lastLocation.addOnCompleteListener { task: Task<Location> ->
        if (task.isSuccessful) {
            onResult(task.result)
        } else {
            onResult(null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApp()
}


