package com.example.kirekikhabi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

abstract class BaseFragment <VB : ViewBinding>
    (private val bindingInflater: (inflater: LayoutInflater)-> VB)
    : Fragment() {
    lateinit var mAuth: FirebaseAuth
    lateinit var mRef : DatabaseReference
    lateinit var sRef : StorageReference

    var firebassuser : FirebaseUser? = null

  private var _binding : VB? = null
    val  binding : VB
    get() = _binding as VB
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)

        mAuth = FirebaseAuth.getInstance()

        mRef = FirebaseDatabase.getInstance().reference

        sRef = FirebaseStorage.getInstance().reference

        firebassuser = FirebaseAuth.getInstance().currentUser

        return binding.root
    }
}
