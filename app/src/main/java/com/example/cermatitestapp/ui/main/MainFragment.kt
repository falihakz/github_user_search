package com.example.cermatitestapp.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.cermatitestapp.R
import com.example.cermatitestapp.databinding.MainFragmentBinding
import com.example.cermatitestapp.ui.common.InfiniteScroll
import kotlinx.android.synthetic.main.search_toolbar.view.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private lateinit var binding: MainFragmentBinding
    private val adapter: UserListAdapter by lazy {
        UserListAdapter(Glide.with(this))
    }

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBinding()
        initViews()
        initObserver()
    }

    private fun initObserver() {
        viewModel.userSearchResults.observe(viewLifecycleOwner, Observer {
            adapter.setItems(it)
        })
        viewModel.loadingUserListEvent.observe(viewLifecycleOwner, Observer {isLoading ->
            if(isLoading != null && isLoading == true){
                if (viewModel.mPage > 1){
                    binding.tvLoadingContainer.visibility = View.GONE
                    binding.tvLoadMoreContainer.visibility = View.VISIBLE
                } else {
                    binding.tvLoadingContainer.visibility = View.VISIBLE
                    binding.tvLoadMoreContainer.visibility = View.GONE
                }
            } else {
                binding.tvLoadingContainer.visibility = View.GONE
                binding.tvLoadMoreContainer.visibility = View.GONE
            }
        })
    }

    private fun initBinding() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
    }

    private fun initViews() {
        binding.includeToolbar.toolbarSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.rvUserList.clearOnScrollListeners()
                binding.rvUserList.addOnScrollListener(object: InfiniteScroll(){
                    override fun onLoadMore() {
                        viewModel.loadNextPage()
                    }
                })
                viewModel.setKeyword(v.text.toString())
            }
            false
        }
        binding.includeToolbar.toolbarClose.setOnClickListener {
            binding.rvUserList.clearOnScrollListeners()
            binding.includeToolbar.toolbarSearch.setText("")
            viewModel.resetSearch()
        }

        binding.errorWarningContainer.setOnClickListener {
            viewModel.loadNextPage()
        }

        binding.rvUserList.layoutManager = LinearLayoutManager(context)
        binding.rvUserList.adapter = adapter

    }

}
