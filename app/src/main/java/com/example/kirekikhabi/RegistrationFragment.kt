package com.example.kirekikhabi

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.example.kirekikhabi.databinding.FragmentRegistrationBinding

class RegistrationFragment :BaseFragment<FragmentRegistrationBinding>(FragmentRegistrationBinding::inflate) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.registrationBtn.setOnClickListener {
            var name = binding.editTextUsername.text.toString().trim()
            var email = binding.editTextEmail.text.toString().trim()
            var password = binding.editTextNewPassword.text.toString().trim()
            userRegistration(name,email,password)
        }
        binding.alreadyHaveAnAccount.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun userRegistration(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener { result->

            var user = User(
                name = name ,
                email = email,
                password = password,
                profileImage = "",
                userId = result.user!!.uid )

            mRef.child("User").setValue(user.userId).addOnSuccessListener {
                mAuth.signOut()
                findNavController().popBackStack()
            }.addOnFailureListener {error->
                var alertDialog = AlertDialog.Builder(requireActivity()).setTitle("Error")
                    .setMessage(error.message)
                alertDialog.create().show()
            }












              //  userId = result.user!!.uid)
            //we have just connected these things here

            mAuth.signOut()
            findNavController().popBackStack()
        }.addOnFailureListener { error->
           var alertDialog = AlertDialog.Builder(requireActivity()).setTitle("Error")
               .setMessage(error.message)
            alertDialog.create().show()
        }
    }
}



