package com.bharatsave.goldapp.view.main.games

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bharatsave.goldapp.databinding.FragmentGameBinding
import com.bharatsave.goldapp.model.game.GameModel
import com.bharatsave.goldapp.model.game.Orientation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private lateinit var gameAdapter: GameAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupViews()
        // TODO: remove this, temporary!!
        temp()
    }

    // TODO: remove this
    private fun temp() {
        binding.tvTokenAvailable.text = "80"
        binding.tvTokenSpent.text = "420"

        binding.rvGames.visibility = View.VISIBLE
        (binding.rvGames.adapter as GameAdapter).setData(
            listOf(
                GameModel(
                    "Circus",
                    "http://gamegur-us.github.io/circushtml5",
                    20,
                    50,
                    Orientation.LANDSCAPE
                ),
                GameModel(
                    "DodgeIt",
                    "https://devansh-299.itch.io/dodgeit",
                    40,
                    80,
                    Orientation.PORTRAIT
                ),
                GameModel(
                    "Elemental One",
                    "http://voithos.io/elemental-one/",
                    40,
                    80,
                    Orientation.LANDSCAPE
                )
            )
        )
    }

    private fun setupViews() {
        gameAdapter = GameAdapter()
        binding.rvGames.apply {
            visibility = View.GONE
            adapter = gameAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}