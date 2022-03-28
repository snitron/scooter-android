package com.development.sota.scooter.ui.tutorial.presentation

import android.content.Intent
import android.os.Bundle
import com.development.sota.scooter.MainActivity
import com.development.sota.scooter.databinding.ActivityTutorialBinding
import moxy.MvpAppCompatActivity
import moxy.MvpView
import moxy.ktx.moxyPresenter
import moxy.viewstate.strategy.alias.AddToEnd


interface TutorialView : MvpView {
    @AddToEnd
    fun nextPage(index: Int)

    @AddToEnd
    fun finishActivity()
}

class TutorialActivity : MvpAppCompatActivity(), TutorialView {
    private val presenter by moxyPresenter { TutorialPresenter(this) }

    private var _binding: ActivityTutorialBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTutorialBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.viewPager2Tutorial.adapter = TutorialAdapter()
        binding.viewPager2Tutorial.isUserInputEnabled = false

        binding.buttonTutorialNext.setOnClickListener { presenter.onNextButtonClicked(binding.viewPager2Tutorial.currentItem) }

        binding.buttonTutorialSkip.setOnClickListener { presenter.onSkipButtonClicked() }

        binding.springDotsIndicatorTutorial.setViewPager2(binding.viewPager2Tutorial)
    }

    override fun nextPage(index: Int) {
        runOnUiThread {
            binding.viewPager2Tutorial.setCurrentItem(index, true)
        }
    }

    override fun finishActivity() {
        runOnUiThread {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}