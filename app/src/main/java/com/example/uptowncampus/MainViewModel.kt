package com.example.uptowncampus

import android.content.ContentValues.TAG
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uptowncampus.dto.*
import com.example.uptowncampus.ui.theme.service.BuildingService
import com.example.uptowncampus.ui.theme.service.IBuildingService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import org.json.JSONException

class MainViewModel(var buildingService : IBuildingService = BuildingService()) : ViewModel() {

    val eventPhotos: MutableLiveData<List<Photo>> = MutableLiveData<List<Photo>>()
    val photos: ArrayList<Photo> by mutableStateOf(ArrayList<Photo>())
    val NEW_BUILDING = "New Building"
    var buildings: MutableLiveData<List<Building>> = MutableLiveData<List<Building>>()
    var savedBuildings: MutableLiveData<List<SavedBuildings>> = MutableLiveData<List<SavedBuildings>>()
    var selectedSavedBuilding by mutableStateOf(SavedBuildings())
    var user : User? = null

    private lateinit var firestore : FirebaseFirestore
    private var storageReference = FirebaseStorage.getInstance().getReference()

    init {
        firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()

    }

      // MB - I was trying to link data to database but we need to fix how our database is setup
    fun listenForSavedBuildings() {
          user?.let {
              user ->
              firestore.collection("users").document(user.uid).collection("buildings")
                  .addSnapshotListener { snapshot, e ->
                      //handle error
                      if (e != null) {
                          Log.w("Listen Failed", e)
                          return@addSnapshotListener
                      }
                      snapshot?.let {
                          val allBuildings = ArrayList<SavedBuildings>()
                          allBuildings.add(SavedBuildings(buildingName = "New Building"))
                          val documents = snapshot.documents
                          documents.forEach {
                              val building = it.toObject(SavedBuildings::class.java)
                              building?.let {
                                  allBuildings.add(building)
                              }
                          }
                          savedBuildings.value = allBuildings
                      }
                  }
          }
    }

    fun fetchBuildings() {
        viewModelScope.launch {
            try{
                buildings.postValue(buildingService.fetchBuilding())
            } catch(e: JSONException){
                Log.e("buildingService", "Failed to fetch buildings")
            }
        }
    }

    // -RS- Commented out this function for when the database has been restructured.
//    fun save(studentComment: StudentComment) {
//        val document = if (studentComment.commentId.isEmpty()) {
//            firestore.collection("comments").document()
//        } else {
//            firestore.collection("comments").document(studentComment.commentId)
//        }
//        studentComment.commentId = document.id
//        val handle = document.set(studentComment)
//        handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
//        handle.addOnFailureListener { Log.e("Firebase", "Save failed $it")}
//    }

    fun saveBuilding() {
        user?.let {
            user ->
            val document =
                if (selectedSavedBuilding.savedBuildingId == null || selectedSavedBuilding.savedBuildingId.isEmpty()) {
                    firestore.collection("users").document(user.uid).collection("buildings").document()
                } else {
                    firestore.collection("users").document(user.uid).collection("buildings").document(selectedSavedBuilding.savedBuildingId)
                }
            selectedSavedBuilding.savedBuildingId = document.id
            val handle = document.set(selectedSavedBuilding)
            handle.addOnSuccessListener {
                Log.d("Firebase", "Document Saved")
                if (photos.isNotEmpty()) {
                    uploadPhotos()
                }
            }
            handle.addOnFailureListener { Log.e("Firebase", "Save failed $it") }
        }
    }

    private fun uploadPhotos() {
        photos.forEach {
            photo ->
            var uri = Uri.parse(photo.localUri)
            val imageRef = storageReference.child("images/${user?.uid}/${uri.lastPathSegment}")
            val uploadTask = imageRef.putFile(uri)
            uploadTask.addOnSuccessListener {
                Log.i(TAG, "Image Uploaded $imageRef")
                val downloadUrl = imageRef.downloadUrl
                downloadUrl.addOnSuccessListener {
                    remoteUri ->
                    photo.remoteUri = remoteUri.toString()
                    updatePhotoDatabase(photo)
                }
            }
            uploadTask.addOnFailureListener{
                Log.e(TAG, it.message ?: "No message")
            }
        }
    }

    internal fun updatePhotoDatabase(photo: Photo) {
        user?.let { user ->
            var photoDocument =
                if (photo.id.isEmpty()) {
                    firestore.collection("users").document(user.uid).collection("buildings")
                        .document(selectedSavedBuilding.savedBuildingId).collection("photos").document()
                } else {
                    firestore.collection("users").document(user.uid).collection("buildings")
                        .document(selectedSavedBuilding.savedBuildingId).collection("photos").document(photo.id)
                }
            photo.id = photoDocument.id

            var handle = photoDocument.set(photo)
            handle.addOnSuccessListener {
                Log.i(TAG, "Successfully updated photo metadata")
            }
            handle.addOnFailureListener {
                Log.e(TAG, "Error updating photo data: ${it.message}")
            }
        }
    }

    fun saveUser () {
        user?.let {
            user ->
            val handle = firestore.collection("users").document(user.uid).set(user)
            handle.addOnSuccessListener { Log.d("Firebase", "Document Saved") }
            handle.addOnFailureListener { Log.e("Firebase", "Save failed $it") }
        }
    }

    fun fetchPhotos() {
        user?.let {
            user ->
            var photoCollection = firestore.collection("users").document(user.uid).collection("buildings")
                .document(selectedSavedBuilding.savedBuildingId).collection("photos")
            var photoListener = photoCollection.addSnapshotListener {
                querySnapshot, firebaseFirestoreException ->
                querySnapshot?.let {
                    querySnapshot ->
                    var documents = querySnapshot.documents
                    var inPhotos = ArrayList<Photo>()
                    documents?.forEach {
                        var photo = it.toObject(Photo::class.java)
                        photo?.let {
                            inPhotos.add(it)
                        }
                    }
                    eventPhotos.value = inPhotos
                }
            }
        }

    }

    fun delete(photo: Photo) {
        user?.let {
            user ->
            var photoCollection = firestore.collection("users").document(user.uid).collection("buildings")
                .document(selectedSavedBuilding.savedBuildingId).collection("photos")
            photoCollection.document(photo.id).delete()
            val uri = Uri.parse(photo.localUri)
            val imageRef = storageReference.child("images/${user.uid}/${uri.lastPathSegment}")
            imageRef.delete()
                .addOnSuccessListener {
                    Log.i(TAG, "Photo binary file deleted ${photo}")
                }
                .addOnFailureListener {
                    Log.e(TAG, "Photo delete failed.   ${it.message}")
                }
        }

    }
}