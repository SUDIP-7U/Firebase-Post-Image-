package com.example.kirekikhabi

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kirekikhabi.databinding.AddPostDialogBinding
import com.example.kirekikhabi.databinding.FragmentHomeBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class HomeFragment:BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    val userList: MutableList<User> = mutableListOf()

    val postList : MutableList<PostWithUser> = mutableListOf()

    lateinit var adapter : PostAdapter

    val posImageLink = MutableLiveData<String>()

    val postBinding: AddPostDialogBinding = AddPostDialogBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setData()
        getalluser()
        addbottomsheetdialogue()
    }
    private fun getalluser() {

        mRef.child("User").addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                for (sn in snapshot.children) {
                    val user: User = sn.getValue(User::class.java)!!
                    userList.add(user)
                }
                Log.d("TAG", "size: ${userList.size} ")
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun setData() {
        mRef.child("Post").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                postList.clear()
          for (sn in snapshot.children) {
              val post = sn.getValue(Post::class.java)
              setuserwithpost(post!!)
          }
                adapter = PostAdapter(postList)
                val manager = LinearLayoutManager(requireContext())
                binding.postRcv.layoutManager = manager
                binding.postRcv.adapter = adapter
     }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun addbottomsheetdialogue() {

        val postDialog = BottomSheetDialog(requireContext())

        postDialog.setCancelable(true)

        postDialog.setCanceledOnTouchOutside(true)

        postDialog.setContentView(postBinding.root)

         val startForProfileImageResult =

            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                val resultCode = result.resultCode

                val data = result.data

                if (resultCode == Activity.RESULT_OK) {

                    val fileUri = data?.data!!

                    val myRef = sRef.child("post").child("post_${System.currentTimeMillis()}.jpg")

                    myRef.putFile(fileUri).addOnCompleteListener { task ->

                        if (task.isSuccessful) {

                            myRef.downloadUrl.addOnSuccessListener { link ->

                                posImageLink.postValue(link.toString())

                            }
                        }
                    }

                    postBinding.postImage.setImageURI(fileUri)

                } else if (resultCode == ImagePicker.RESULT_ERROR) {

                    Toast.makeText(requireContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show()

                } else {

                    Toast.makeText(requireContext(), "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }

        postBinding.postImage.setOnClickListener {
            ImagePicker.with(this)
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        val post = Post(mAuth.uid.toString(),"","","")

        posImageLink.observe(viewLifecycleOwner) {

            if (it is String) {

                postBinding.btnUploadPost.visibility = View.VISIBLE

            }else{

                postBinding.btnUploadPost.visibility = View.INVISIBLE
            }
        }

        postBinding.apply {

            btnUploadPost.setOnClickListener {

                val content = etPost.text.toString().trim()

                Toast.makeText(requireContext(), "${content}", Toast.LENGTH_LONG).show()

                val postId = mRef.push().key

                post.postContent=content

                post.postID = postId!!

                mRef.child("Post").child(postId).setValue(post).addOnSuccessListener {
                    Toast.makeText(requireContext(), "MILK", Toast.LENGTH_LONG).show()
                    postDialog.dismiss()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "MilKI", Toast.LENGTH_LONG).show()
                    postDialog.dismiss()
                }
            }
        }
    }

    private fun setuserwithpost(post: Post?) {

        post?.authorId?.let {

            mRef.child("User").child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user: User = snapshot.getValue(User::class.java)!!
                    val postWithUser = PostWithUser(post, user )
                    postList.add(postWithUser)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}