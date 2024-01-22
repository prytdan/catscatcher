package prytdan.catscatcher.presentation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel> : Fragment() {

    abstract val viewModel: VM

    private var viewBinding: VB? = null

    val binding get() = viewBinding!!

    abstract fun getViewBinding(): VB

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = getViewBinding()
        return viewBinding?.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        viewModel.attachView()
    }

    protected open fun initViews() {
    }
}