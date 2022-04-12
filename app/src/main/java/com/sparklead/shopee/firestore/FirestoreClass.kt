package com.sparklead.shopee.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sparklead.shopee.ui.activities.Constants
import com.sparklead.shopee.ui.activities.LoginActivity
import com.sparklead.shopee.ui.activities.Profile
import com.sparklead.shopee.ui.activities.RegisterActivity
import com.sparklead.shopee.models.User

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()


    fun registerUser(activity: RegisterActivity, userInfo: User){

        //the "users" is a collection name.
        mFirestore.collection(Constants.USERS)
            //Document Id for users fields.
            .document(userInfo.id)
            //here the userInfo are field and the setOption is set to merge.
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {

                //Here call a function of base activity for transferring the result to it.
                activity.userRegistrationSuccess()
            }


            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while registering the user.",
                    e
                )
            }

    }

    private fun getCurrentUserId() :String {

        //An Instance of currentUser using FirebaseAuth
        val currentUser = FirebaseAuth.getInstance().currentUser

        //A variable to assign the currentUserId if it is not null or else it will be blank.
        var currentUserId = ""
        if(currentUser!=null)
        {
            currentUserId = currentUser.uid
        }

        return currentUserId

    }

    fun getUserDetails(activity: Activity){

        //Here we pass the collection name from which we wants the data.
        mFirestore.collection(Constants.USERS)

            // the document id to get the field of User.
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                Log.i(activity.javaClass.simpleName,document.toString())

                //Here we have received the document snapshot which is converted into the User data model object.
                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.SHOPEE_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor :SharedPreferences.Editor = sharedPreferences.edit()
                //key : logged_in_success :Aditya Gupta
                editor.putString(Constants.LOGGED_IN_USERNAME," ${user.firstName} ${user.lastName}")
                editor.apply()
                //end

                when(activity){
                    is LoginActivity ->
                    {
                        //call a function of base activity for transferring the result to it
                        activity.userLoggedInSuccess(user)

                    }
                }
            }

        //end
    }

    fun updateUserProfileData(activity: Activity,userHashMap: HashMap<String,Any>)
    {
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnCompleteListener{
                when(activity)
                {
                    is Profile ->
                    {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener{ e ->
                when(activity)
                {
                    is Profile ->
                    {
                        activity.hideProgressDialog()
                    }
                }
                Log.e(
                    activity.javaClass.simpleName,"Error while updating the user details",e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            Constants.USER_PROFILE_IMAGE + System.currentTimeMillis() + "."
                    + Constants.getFileExtension(
                activity,
                imageFileURI
            )
        )

        //adding the file to reference
        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                // The image upload is success
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                // Get the downloadable url from the task snapshot
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        // TODO Step 8: Pass the success result to base class.
                        // START
                        // Here call a function of base activity for transferring the result to it.
                        when (activity) {
                            is Profile -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                        // END
                    }
            }
            .addOnFailureListener { exception ->

                // Hide the progress dialog if there is any error. And print the error in log.
                when (activity) {
                    is Profile -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("ABCD",
                    exception.message,
                    exception
                )
            }
    }
}