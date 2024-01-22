package prytdan.catscatcher.presentation.fragments.game

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Rect
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import prytdan.catscatcher.R
import prytdan.catscatcher.databinding.FragmentGameBinding
import prytdan.catscatcher.presentation.fragments.BaseFragment
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.abs

class GameFragment : BaseFragment<FragmentGameBinding, GameViewModel>(), SensorEventListener {

    private companion object {
        const val SENSITIVITY = 0.02f
        const val SENSITIVITY_THRESHOLD = 0.5f
        const val CATS_SIZE_DP = 100
        const val DELAY_BETWEEN_CATS = 1000L
    }

    override val viewModel: GameViewModel by viewModel()

    private var isGameActive = false

    private lateinit var sensorManager: SensorManager

    private var accelerometerSensor: Sensor? = null

    private var gameJob: Job? = null

    private val activeCatAnimations: MutableList<ValueAnimator> = CopyOnWriteArrayList()

    private val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.gameFragment, true)
        .build()

    private val gameCountdownTimer = object : CountDownTimer(4000, 1000) {

        override fun onTick(millisUntilFinished: Long) {
            binding.textCountdownTimer.visibility = View.VISIBLE
            binding.textCountdownTimer.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            if (isAdded) {
                binding.textCountdownTimer.visibility = View.GONE
                subscribeToViewModel()
                setupControls()
                startGame()
            }
        }
    }

    override fun getViewBinding() = FragmentGameBinding.inflate(layoutInflater)

    override fun initViews() {
        overrideBackPressed()
        setupSensor()
        gameCountdownTimer.start()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let { moveBed(it.values[0]) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // no adjustment needed
    }

    override fun onResume() {
        super.onResume()
        accelerometerSensor?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    private fun moveBed(xAxisMovement: Float) {
        if (isGameActive && abs(xAxisMovement) > SENSITIVITY_THRESHOLD) {
            val currentX = binding.imageBed.x
            val screenWidth = binding.root.width
            val bedWidth = binding.imageBed.width
            val displacement = -xAxisMovement * SENSITIVITY * screenWidth
            val newX = (currentX + displacement).coerceIn(0f, (screenWidth - bedWidth).toFloat())
            binding.imageBed.x = newX
        }
    }

    private fun setupSensor() {
        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun setupControls() {
        binding.buttonPause.setOnClickListener {
            stopGame()
            pauseCatAnimations()
            showPauseDialog()
        }
    }

    private fun subscribeToViewModel() {
        viewModel.scoresLiveData.observe(viewLifecycleOwner) { scores ->
            binding.textScoresNumber.text = scores.toString()
        }
    }

    private fun startGame() {
        isGameActive = true
        gameJob = lifecycleScope.launch {
            while (isActive) {
                delay(DELAY_BETWEEN_CATS)
                spawnCat()
            }
        }
    }

    private fun stopGame() {
        isGameActive = false
        gameJob?.cancel()
        gameJob = null
    }

    private fun spawnCat() {
        val catImageView = ImageView(requireContext()).apply {
            layoutParams =
                ConstraintLayout.LayoutParams(CATS_SIZE_DP.dpToPx(), CATS_SIZE_DP.dpToPx())
            setImageResource(viewModel.getRandomCatDrawable())
        }

        catImageView.x = viewModel.getRandomX(binding.root.width, catImageView.layoutParams.width)
        catImageView.y = -binding.root.height.toFloat()
        binding.root.addView(catImageView)
        animateCatFall(catImageView)
    }

    private fun animateCatFall(catImageView: ImageView) {
        val animator = ValueAnimator.ofFloat(
            -binding.root.height.toFloat(),
            binding.root.height.toFloat() + 200
        ).apply {
            duration = viewModel.getCurrentFallingSpeed()
            addUpdateListener { animation ->
                catImageView.translationY = animation.animatedValue as Float
                if (isCatCaught(catImageView)) {
                    removeCat(catImageView, this)
                    viewModel.increaseFallingSpeed()
                    viewModel.incrementScores()
                }
                if (animation.animatedFraction == 1.0f && !isCatCaught(catImageView)) {
                    removeCat(catImageView, this)
                    stopGame()
                    clearCatAnimations()
                    viewModel.saveGameResultScores()
                    showGameOverDialog()
                }
            }
            addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator) {
                    activeCatAnimations.remove(this@apply)
                }

                override fun onAnimationCancel(p0: Animator) {
                    // not needed
                }

                override fun onAnimationRepeat(p0: Animator) {
                    // not needed
                }

                override fun onAnimationStart(animation: Animator) {
                    activeCatAnimations.add(this@apply)
                }
            })
        }
        animator.start()
    }

    private fun pauseCatAnimations() {
        for (animator in activeCatAnimations) {
            animator.pause()
        }
    }

    private fun resumeCatAnimations() {
        for (animator in activeCatAnimations) {
            animator.resume()
        }
    }

    private fun clearCatAnimations() {
        for (animator in activeCatAnimations) {
            animator.cancel()
        }
        activeCatAnimations.clear()
    }

    private fun removeCat(catImageView: ImageView, animator: ValueAnimator) {
        binding.root.removeView(catImageView)
        animator.cancel()
    }

    private fun isCatCaught(catImageView: ImageView): Boolean {
        val bedBounds = Rect()
        binding.imageBed.getHitRect(bedBounds)
        val catBounds = Rect()
        catImageView.getHitRect(catBounds)
        return Rect.intersects(bedBounds, catBounds)
    }

    @SuppressLint("InflateParams")
    private fun showGameOverDialog() {
        val dialogInflater = layoutInflater.inflate(R.layout.game_over_dialog, null)
        val dialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(dialogInflater)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.show()
        dialog.findViewById<TextView>(R.id.text_scores_number).text =
            viewModel.getScores().toString()
        dialog.findViewById<Button>(R.id.button_menu).setOnClickListener {
            dialog.dismiss()
            resetGameAndNavigateTo(R.id.action_gameFragment_to_menuFragment)
        }
        dialog.findViewById<Button>(R.id.button_restart).setOnClickListener {
            dialog.dismiss()
            resetGameAndNavigateTo(R.id.action_gameFragment_self)
        }
    }

    @SuppressLint("InflateParams")
    private fun showPauseDialog() {
        val dialogInflater = layoutInflater.inflate(R.layout.pause_dialog, null)
        val dialog =
            Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
        dialog.setContentView(dialogInflater)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.setCancelable(false)
        dialog.show()
        dialog.findViewById<Button>(R.id.button_to_menu).setOnClickListener {
            dialog.dismiss()
            resetGameAndNavigateTo(R.id.action_gameFragment_to_menuFragment)
        }
        dialog.findViewById<Button>(R.id.button_resume).setOnClickListener {
            dialog.dismiss()
            resumeCatAnimations()
            startGame()
        }
    }

    private fun resetGameAndNavigateTo(fragmentId: Int) {
        viewModel.resetGameValues()
        findNavController().navigate(fragmentId, null, navOptions)
    }

    private fun overrideBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                stopGame()
                clearCatAnimations()
                resetGameAndNavigateTo(R.id.action_gameFragment_to_menuFragment)
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}