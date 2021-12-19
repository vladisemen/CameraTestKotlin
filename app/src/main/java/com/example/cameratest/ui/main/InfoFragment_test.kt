package com.example.cameratest.ui.main

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameratest.R
import com.example.cameratest.models.Articles
import com.example.cameratest.models.Headlines
import com.example.cameratest.models.NewsAdapter

import java.io.IOException

import com.google.gson.Gson
import kotlinx.android.synthetic.main.info_fragment_test.*
import okhttp3.*
import java.text.SimpleDateFormat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [InfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InfoFragmentTest : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var newsList: RecyclerView? = null
    private val clientHews = OkHttpClient()
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.info_fragment_test, container, false)

        newsList = root.findViewById(R.id.activity_list_news)
        try {
            //activity?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.VISIBLE val
            val strToReq =
                "https://newsapi.org/v2/everything?q=doctor&from=" + SimpleDateFormat("yyyy-MM-dd") + "&sortBy=publishedAt&apiKey=51a8917393e147778b3727c00f20325b"
            run(strToReq)
        } catch (e: Exception) {

        }
        // val toolBar = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        //toolBar?.setNavigationIcon(null);
        return root
    }


    fun setAdapter(response: Response) {
        val stingResponse = response.body?.string()
        val newsList = Gson().fromJson(stingResponse, Headlines::class.java)

        requireActivity().runOnUiThread {
            //activity?.findViewById<ProgressBar>(R.id.progressBar)?.visibility = View.GONE
            try {
                activity_list_news.layoutManager = LinearLayoutManager(activity)
                activity_list_news.adapter =
                    activity?.let { NewsAdapter(newsList, it, this::openActivity) }
            } catch (e: Exception) {
            }

        }
    }



    fun openActivity(view: View, category: Articles) {

    }


    private fun run(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        clientHews.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) = setAdapter(response)
        })
    }


    fun setAlert(str: String, nameTitle: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(nameTitle)
        builder.setMessage(str)
//builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Toast.makeText(
                context,
                android.R.string.yes, Toast.LENGTH_SHORT
            ).show()
        }

        builder.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InfoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InfoFragmentTest().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}