package com.example.inzynierka.fragmenty.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.inzynierka.data.Box
import com.example.inzynierka.data.BoxS
import com.example.inzynierka.data.Pack
import com.example.inzynierka.data.User
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class FirebaseRepository {

    private val REPO_DEBUG = "REPO_DEBUG"

    private val storage= FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val cloud = FirebaseFirestore.getInstance()


    //Funkcja zwracająca informację o użytkowniku który aktualnie jest zalogowany
    fun getUserData(): LiveData<User> {
        val cloudResult = MutableLiveData<User>()
        val uid = auth.currentUser?.uid
        cloud.collection("user")
            .document(uid!!)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                cloudResult.postValue(user)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
        return cloudResult
    }

    fun createNewUser(user: User){
        cloud.collection("user")
            .document(user.uid!!)
            .set(user)
    }

    fun getPackData(id:String): LiveData<Pack> {
        val cloudResult = MutableLiveData<Pack>()
        cloud.collection("pack")
            .document(id)
            .get()
            .addOnSuccessListener {
                val pack = it.toObject(Pack::class.java)
                cloudResult.postValue(pack)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
        return cloudResult
    }

    fun packToMe(): LiveData<String> {
        val uid = auth.currentUser?.uid
        val cloudResult = MutableLiveData<String>()
        var Id_mypack = String()
        cloud.collection("user")
            .document(uid!!)
            .get()
            .addOnSuccessListener {
                val pack = it.toObject(User::class.java)
                if (pack != null) {
                    Log.d(REPO_DEBUG, pack.toMePackID.toString())
                    Id_mypack = pack.toMePackID.toString()
                }
                cloudResult.postValue(Id_mypack)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
        return cloudResult
    }

    //Funkcja do której dostarczamy id paczki
    //Po id paczki funkcja wyszukuje jej pozostałe dane i zwracamy (później z tych danych wyciągamy rozmiar paczki)
    fun PutPack(id:String): LiveData<Pack>{
        val cloudResult = MutableLiveData<Pack>()
        val uid = id
        cloud.collection("pack")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val paczka = it.toObject(Pack::class.java)
                cloudResult.postValue(paczka)
            }
            .addOnFailureListener {exc ->
                Log.d(REPO_DEBUG, exc.message.toString())

                //Log.d(REPO_DEBUG, it.message.toString())
            }
        return cloudResult
    }

    fun findEmptyBoxS(size:String): LiveData<String>
    {
        var emptyBox = null.toString()
        var zwrotEmptyBox = MutableLiveData<String>()
        Log.d("Jestem TUTAJ",size.toString())
        cloud.collection(size)
            .document(1.toString())
            .get()
            .addOnSuccessListener {
                val skrytka = it.toObject(BoxS::class.java)
                //Sprawdzamy która skrytki JEDNEGO rozmiaru  jest wolna jeżeli jest kilka to funkcja zwraca pierwszą wolną
                //Wyciągamy stringa stanu skrytki i jeżeli równa się on 0 to warunek jest spełniony
                if (skrytka != null) {
                    if(skrytka.FE == 0)
                    {
                        Log.d(1.toString() + " skrytka jest wolna", skrytka.FE.toString())

                        if(emptyBox == null.toString()){
                            zwrotEmptyBox.setValue(1.toString())
                            emptyBox=1.toString()
                        }

                    }
                    else
                    {
                        Log.d(1.toString() + " skrytka jest zajęta", skrytka.FE.toString())

                    }
                }

            }
            .addOnFailureListener {exc->
                Log.d(REPO_DEBUG, exc.message.toString())
            }
        cloud.collection(size)
            .document(2.toString())
            .get()
            .addOnSuccessListener {
                val skrytka = it.toObject(BoxS::class.java)
                //Sprawdzamy która skrytki JEDNEGO rozmiaru  jest wolna jeżeli jest kilka to funkcja zwraca pierwszą wolną
                //Wyciągamy stringa stanu skrytki i jeżeli równa się on 0 to warunek jest spełniony
                if (skrytka != null) {
                    if(skrytka.FE == 0)
                    {
                        Log.d(2.toString() + " skrytka jest wolna", skrytka.FE.toString())
                        if(emptyBox == null.toString()){
                            zwrotEmptyBox.setValue(2.toString())
                            emptyBox=2.toString()
                        }
                    }
                    else
                    {
                        Log.d(2.toString() + " skrytka jest zajęta", skrytka.FE.toString())

                    }
                }

            }
            .addOnFailureListener {exc->
                Log.d(REPO_DEBUG, exc.message.toString())
            }
        cloud.collection(size)
            .document(3.toString())
            .get()
            .addOnSuccessListener {
                val skrytka = it.toObject(BoxS::class.java)
                //Sprawdzamy która skrytki JEDNEGO rozmiaru  jest wolna jeżeli jest kilka to funkcja zwraca pierwszą wolną
                //Wyciągamy stringa stanu skrytki i jeżeli równa się on 0 to warunek jest spełniony
                if (skrytka != null) {
                    if(skrytka.FE == 0)
                    {
                        Log.d(3.toString() + " skrytka jest wolna", skrytka.FE.toString())
                        if(emptyBox == null.toString()){
                            zwrotEmptyBox.setValue(3.toString())
                            emptyBox=3.toString()
                        }
                    }
                    else
                    {
                        Log.d(3.toString() + " skrytka jest zajęta", skrytka.FE.toString())

                    }
                }

            }
            .addOnFailureListener {exc->
                Log.d(REPO_DEBUG, exc.message.toString())
            }
        cloud.collection(size)
            .document(4.toString())
            .get()
            .addOnSuccessListener {
                val skrytka = it.toObject(BoxS::class.java)
                //Sprawdzamy która skrytki JEDNEGO rozmiaru  jest wolna jeżeli jest kilka to funkcja zwraca pierwszą wolną
                //Wyciągamy stringa stanu skrytki i jeżeli równa się on 0 to warunek jest spełniony
                if (skrytka != null) {
                    if(skrytka.FE == 0)
                    {
                        Log.d(4.toString() + " skrytka jest wolna", skrytka.FE.toString())
                        if(emptyBox == null.toString()){
                            zwrotEmptyBox.setValue(4.toString())
                            emptyBox=4.toString()
                        }
                    }
                    else
                    {
                        Log.d(4.toString() + " skrytka jest zajęta", skrytka.FE.toString())

                    }
                }

            }
            .addOnFailureListener {exc->
                Log.d(REPO_DEBUG, exc.message.toString())
            }
        cloud.collection(size)
            .document(5.toString())
            .get()
            .addOnSuccessListener {
                val skrytka = it.toObject(BoxS::class.java)
                //Sprawdzamy która skrytki JEDNEGO rozmiaru  jest wolna jeżeli jest kilka to funkcja zwraca pierwszą wolną
                //Wyciągamy stringa stanu skrytki i jeżeli równa się on 0 to warunek jest spełniony
                if (skrytka != null) {
                    if(skrytka.FE == 0)
                    {
                        Log.d(5.toString() + " skrytka jest wolna", skrytka.FE.toString())
                        if(emptyBox == null.toString()){
                            zwrotEmptyBox.setValue(5.toString())
                            emptyBox=5.toString()
                        }
                    }
                    else
                    {
                        Log.d(5.toString() + " skrytka jest zajęta", skrytka.FE.toString())

                    }
                }

            }
            .addOnFailureListener {exc->
                Log.d(REPO_DEBUG, exc.message.toString())
            }

        return zwrotEmptyBox
    }



    //Funkcja wysyłająca informację do skrytki o danym id aby ją otworzyć
    fun editBoxData(size: String, id: String, packID: String){
        cloud.collection(size)
            .document(id)
            .update("OC",1, "ID", packID)
            .addOnSuccessListener{
               Log.d("Zaktualizowano dane skrytki ", id)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    fun editUserData(Uid: String, packID: String){
        cloud.collection("user")
            .document(Uid)
            .update("check",1, "toMePackID", packID.toString())
            .addOnSuccessListener{
                Log.d("Zaktualizowano dane uzytkownika ", Uid)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    fun openBoxCT(size: String, id: String){
        cloud.collection(size)
            .document(id)
            .update("OC",1)
            .addOnSuccessListener{
                Log.d("Zaktualizowano dane skrytki ", id)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    fun openBox(size: String, id: String){
        var rozmiar = String()
        if(size==1.toString())
        {
            rozmiar = "box"
        }
        cloud.collection(rozmiar)

            .document(id)
            .update("OC",1)
            .addOnSuccessListener{
                Log.d("Zaktualizowano dane skrytki ", id)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    //Funkcja zmieniająca w bazie danych informację użytkownikowi że odstał paczkę o danym numerze ID
    fun sendInfoToUser(Id_pack: String, uid:String)
    {
        cloud.collection("user")
            .document(uid)
            .update("toMePackID",Id_pack)
            .addOnSuccessListener{
                Log.d("Zaktualizowano informację o paczce. Jej numer ID to ", Id_pack)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    fun editBoxFullData(size: String, id: String){
        cloud.collection(size)
            .document(id)
            .update("FE",1)
            .addOnSuccessListener{
                Log.d("Zaktualizowano dane skrytki ", id)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    fun editBoxEmptyData(size: String, id: String){
        cloud.collection(size)
            .document(id)
            .update("FE",0, "ID", "")
            .addOnSuccessListener{
                Log.d("Zaktualizowano dane skrytki ", id)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    fun closeBox(size: String, id:String)
    {
        cloud.collection(size)
            .document(id)
            .update("OC",0)
            .addOnSuccessListener{
                Log.d("Zaktualizowano dane skrytki ", id)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }

    fun editPackData(numerIdPack: String, numerIdBox: String)
    {
        cloud.collection("pack")
            .document(numerIdPack)
                //packInBox jeżeli 1 to paczka jest w jakims boxie
                //Id boxu oznacza w jakiej skrytce znajduje się paczka
                //Uproszczenie skrytki od 1 do 5 to małe do 5 do... itd
            .update("packInBox","1","Id_box",numerIdBox)
            .addOnSuccessListener{
                Log.d("Zaktualizowano dane paczki ", numerIdPack)
            }
            .addOnFailureListener{
                Log.d(REPO_DEBUG, it.message.toString())
            }
    }
}