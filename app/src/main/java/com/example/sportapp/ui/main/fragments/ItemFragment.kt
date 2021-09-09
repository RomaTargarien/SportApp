package com.example.sportapp.ui.main.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.navArgs
import com.example.sportapp.R
import com.example.sportapp.databinding.FragmentItemBinding


class ItemFragment : Fragment() {

    val args: ItemFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentItemBinding.inflate(inflater,container,false)
        val link = args.link
        binding.webView.apply {
            webViewClient = WebViewClient()
            loadUrl(link)
        }
        binding.webView
        return binding.root
    }

}