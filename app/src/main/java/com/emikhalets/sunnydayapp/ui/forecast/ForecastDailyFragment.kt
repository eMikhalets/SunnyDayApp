package com.emikhalets.sunnydayapp.ui.forecast

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.emikhalets.sunnydayapp.adapters.DailyAdapter
import com.emikhalets.sunnydayapp.data.network.pojo.ResponseDaily
import com.emikhalets.sunnydayapp.databinding.FragmentForecastDailyBinding
import com.emikhalets.sunnydayapp.utils.CURRENT_QUERY
import timber.log.Timber

class ForecastDailyFragment : Fragment() {

    private var _binding: FragmentForecastDailyBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DailyAdapter
    private lateinit var viewModel: ForecastDailyViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentForecastDailyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ForecastDailyViewModel::class.java)
        adapter = DailyAdapter(ArrayList())
        binding.listForecastDaily.adapter = adapter
        observeData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun observeData() {
        CURRENT_QUERY.observe(viewLifecycleOwner, Observer {
            hideForecastList()
            hideNotice()
            showProgressbar()
            viewModel.requestForecastDaily(it)
        })

        viewModel.forecastDaily.observe(viewLifecycleOwner, Observer {
            adapter.setList(it.data)
            hideProgressbar()
            showForecastList()
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Timber.d("Error loading weather.")
            Timber.d(it)
            hideProgressbar()
            binding.textNotice.text = it
            showNotice()
        })
    }

    private fun showProgressbar() {
        binding.pbLoadingDaily.visibility = View.VISIBLE
    }

    private fun hideProgressbar() {
        binding.pbLoadingDaily.visibility = View.INVISIBLE
    }

    private fun showNotice() {
        binding.textNotice.visibility = View.VISIBLE
    }

    private fun hideNotice() {
        binding.textNotice.visibility = View.INVISIBLE
    }

    private fun showForecastList() {
        binding.listForecastDaily.visibility = View.VISIBLE
    }

    private fun hideForecastList() {
        binding.listForecastDaily.visibility = View.INVISIBLE
    }
}