package com.sparklead.shopee.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sparklead.shopee.models.*
import com.sparklead.shopee.ui.activities.*
import com.sparklead.shopee.ui.fragments.DashboardFragment
import com.sparklead.shopee.ui.fragments.OrdersFragment
import com.sparklead.shopee.ui.fragments.ProductsFragment
import kotlinx.coroutines.flow.merge

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
                    "Error while registering the user_id.",
                    e
                )
            }

    }

    fun getCurrentUserId() :String {

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
                    is SettingActivity ->
                    {
                        activity.userDetailsSuccess(user)
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
                    activity.javaClass.simpleName,"Error while updating the user_id details",e
                )
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri? , imageType : String ) {

        //getting the storage reference
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "."
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
                            is AddProductActivity ->
                            {
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

    fun uploadProductDetails(activity: AddProductActivity,productInfo: Product){
        mFirestore.collection(Constants.PRODUCTS)
            .document()
            .set(productInfo,SetOptions.merge())
            .addOnSuccessListener {

                activity.productUploadSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while uploading the product details. ",
                    e
                )
            }
    }

    fun getProductList(fragment: Fragment){
        mFirestore.collection(Constants.PRODUCTS)

            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                Log.e("Product List",document.documents.toString())
                val productsList :ArrayList<Product> = ArrayList()
                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id

                    productsList.add(product)
                }

                when(fragment){
                    is ProductsFragment ->{
                        fragment.successProductListFromFireStore(productsList)

                    }
                }
            }

    }

    fun getProductDetails(activity: ProductDetailsActivity,productId: String)
    {
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document->
                val product = document.toObject(Product::class.java)
                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,"Error while getting the product details",e)

            }
    }

    fun addCartItems(activity: ProductDetailsActivity, addToCart: CartItem)
    {
        mFirestore.collection(Constants.CART_ITEMS)
            .document()
            .set(addToCart, SetOptions.merge())
            .addOnCompleteListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener {

                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item. ",it
                )
            }
    }

    fun getCartList(activity: Activity){

        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                val list: ArrayList<CartItem> = ArrayList()

                for(i in document.documents)
                {
                    val cartItem = i.toObject(CartItem::class.java)!!

                    cartItem.id = i.id
                    list.add(cartItem)
                }

                when(activity){
                    is CartListActivity->
                    {
                        activity.successCartItemList(list)
                    }

                    is CheckoutActivity ->{
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e->
                when(activity)
                {
                    is CartListActivity ->
                    {
                        activity.hideProgressDialog()
                    }

                    is CheckoutActivity ->{
                        activity.hideProgressDialog()
                    }

                }
                Log.e(activity.javaClass.simpleName,"Error while getting the cart list items.",e)
            }
    }

    fun updateAllDetails(activity: CheckoutActivity,cartList: ArrayList<CartItem>){
        val writeBatch = mFirestore.batch()

        for(cartItem in cartList){
            val productHashMap = HashMap<String,Any>()

            productHashMap[Constants.STOCK_QUANTITY]=(cartItem.stock_quantity.toInt()-cartItem.cart_quantity.toInt()).toString()

            val documentReference = mFirestore.collection(Constants.PRODUCTS)
                .document(cartItem.product_id)
            writeBatch.update(documentReference,productHashMap)
        }

        for(cartItem in cartList){

            val documentReference = mFirestore.collection(Constants.CART_ITEMS)
                .document(cartItem.id)
            writeBatch.delete(documentReference)
        }

        writeBatch.commit()
            .addOnSuccessListener {
                activity.allDetailsUpdatedSuccessfully()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating all the details after order placed"
                )

            }



    }

    fun getMyOrderList(fragment: OrdersFragment){
        mFirestore.collection(Constants.ORDERS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                Log.e(
                    fragment.javaClass.simpleName,document.documents.toString()
                )

                val list:ArrayList<Order> = ArrayList()

                for (i in document.documents){

                    val orderItem = i.toObject(Order::class.java)!!
                    orderItem.id=i.id
                    list.add(orderItem)
                }

                fragment.populateOrdersListUiI(list)
            }
            .addOnFailureListener { e->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.javaClass.simpleName,
                    "Error while getting order list",e
                )

            }
    }

    fun placeOrder(activity: CheckoutActivity ,order : Order){
        mFirestore.collection(Constants.ORDERS)
            .document()
            .set(order, SetOptions.merge())
            .addOnSuccessListener {
                activity.orderPlacedSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while placing order",
                    e
                )
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String)
    {
        mFirestore.collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .whereEqualTo(Constants.PRODUCT_ID,productId)
            .get()
            .addOnSuccessListener{ document->
                
                Log.e(activity.javaClass.simpleName,document.documents.toString())
                if (document.documents.size > 0)
                {
                    activity.productExistingInCart()
                }
                else
                {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                print("Success")
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list",
                    e
                )
            }
    }

    fun deleteProduct(fragment:ProductsFragment,productId:String)
    {
        mFirestore.collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener{
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener{ e->
                fragment.hideProgressDialog()
                Log.e(fragment.requireActivity().javaClass.simpleName,"Error while deleting the product",e)
            }
    }

    fun updateMyCart(context: Context,cart_id:String,itemHashMap: HashMap<String,Any>){
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnCompleteListener {

                when(context){
                    is CartListActivity ->{
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e->
                when(context){
                    is CartListActivity->{
                        context.hideProgressDialog()
                    }
                }

                Log.e(context.javaClass.simpleName,"Error while updating the cart item.",e)
            }
    }

    fun addAddress(activity: AddEditAddressActivity,addressInfo:Address){
            mFirestore.collection(Constants.ADDRESSES)
                .document()
                .set(addressInfo,SetOptions.merge())
                .addOnSuccessListener {
                    activity.addUpdateAddressSuccess()
                }
                .addOnFailureListener { e->

                    activity.hideProgressDialog()
                    Log.e(activity.javaClass.simpleName,"Error while adding the address.",e)
                }
    }

    fun deleteAddress(activity: AddressListActivity,addressId: String){
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName,
                "Error while deleting the address..",
                    e
                )
            }
    }

    fun updateAddress(activity:AddEditAddressActivity,addressInfo: Address,addressId:String){
        mFirestore.collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo,SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e->
                activity.hideProgressDialog()

                Log.e(activity.javaClass.simpleName,
                "Error while updating the address")
            }
    }

    fun getAddressList(activity: AddressListActivity){
        mFirestore.collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.USER_ID,getCurrentUserId())
            .get()
            .addOnSuccessListener { document->
                Log.e(activity.javaClass.simpleName,document.documents.toString())

                val addressList : ArrayList<Address> = ArrayList()

                for(i in document.documents){
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e->

                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting address",
                    e

                )
            }
    }

    fun removeItemFromCart(context: Context, cartItem:String){
        mFirestore.collection(Constants.CART_ITEMS)
            .document(cartItem)
            .delete()
            .addOnCompleteListener {
                when(context){
                    is CartListActivity ->{
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e->
                when(context){
                    is CartListActivity ->{
                        context.hideProgressDialog()
                    }
                }

                Log.e(context.javaClass.simpleName,
                "error while removing the item from the cart list..",e
                )
            }
    }

    fun getAllProductsList(activity: Activity){
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener{ document->
                Log.e("Product List",document.documents.toString())
                val productList:ArrayList<Product> = ArrayList()
                for(i in document.documents)
                {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productList.add(product)
                }

                when(activity){
                    is CartListActivity->{
                        activity.successProductsListFromFirestore(productList)
                    }
                    is CheckoutActivity->{
                        activity.successProductsListFromFirestore(productList)
                    }


                }


            }
            .addOnFailureListener { e->

                when(activity){
                    is CartListActivity->{
                        activity.hideProgressDialog()

                        Log.e(
                            "Get product list","Error while getting all product list.",
                            e
                        )
                    }
                    is CheckoutActivity->{
                        activity.hideProgressDialog()

                        Log.e(
                            "Get product list","Error while getting all product list.",
                            e
                        )
                    }
                }


            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment){
        mFirestore.collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document->

                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents){
                    val product = i.toObject(Product::class.java)
                    product!!.product_id=i.id
                    productsList.add(product)
                }

                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener{ e->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName,"Error while getting dashboard items list",e)
            }
    }
}