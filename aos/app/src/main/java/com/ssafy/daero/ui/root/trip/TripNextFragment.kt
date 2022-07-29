package com.ssafy.daero.ui.root.trip


import android.view.View
import androidx.cardview.widget.CardView
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ssafy.daero.R
import com.ssafy.daero.base.BaseFragment
import com.ssafy.daero.databinding.FragmentTripNextBinding
import com.ssafy.daero.ui.adapter.TripHotAdapter
import com.ssafy.daero.ui.adapter.TripNearByAdapter
import com.ssafy.daero.ui.adapter.TripUntilNowAdapter
import com.ssafy.daero.utils.constant.DEFAULT
import com.ssafy.daero.utils.constant.FAIL
import com.ssafy.daero.utils.constant.SUCCESS
import com.ssafy.daero.utils.hotArticles
import com.ssafy.daero.utils.popularTripPlaces
import com.ssafy.daero.utils.view.toast


class TripNextFragment : BaseFragment<FragmentTripNextBinding>(R.layout.fragment_trip_next) {

    //private val tripNextViewModel: TripNextViewModel by viewModels()
    private lateinit var tripNearByAdapter: TripNearByAdapter
    private lateinit var tripUntilNowAdapter: TripUntilNowAdapter

    private lateinit var bottomSheet: BottomSheetBehavior<CardView>

    override fun init() {
        initAdapter()
        observeData()
        setOnClickListeners()
    }

    private fun initAdapter(){
        tripNearByAdapter = TripNearByAdapter().apply {
            onItemClickListener = nearByTripPlaceClickListener
        }
        binding.recyclerTripNextNearBy.adapter = tripNearByAdapter

        tripUntilNowAdapter = TripUntilNowAdapter().apply {
            onItemClickListener = hotArticleClickListener
        }
        binding.recyclerTripNextNow.adapter = tripUntilNowAdapter
    }

    private val nearByTripPlaceClickListener: (View, Int) -> Unit = { _, tripPlaceSeq ->
        // TODO: 주변 여행지 정보 상세 페이지로 이동
    }

    private val hotArticleClickListener: (View, Int) -> Unit = { _, articleSeq ->
        // TODO: 지금까지 여행지 상세 페이지로 이동
    }

    private fun setOnClickListeners(){

    }

    private fun observeData() {
        // TODO: 주변 여행지 정보 받아오기
        tripNearByAdapter.tripPlaces = popularTripPlaces

        // TODO: 지금까지 여행지 상세 정보 받아오기
        tripUntilNowAdapter.tripPlaces = popularTripPlaces
    }
}