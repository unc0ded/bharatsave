package com.bharatsave.goldapp.view.main.games

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bharatsave.goldapp.databinding.ItemGameBinding
import com.bharatsave.goldapp.model.game.GameModel

class GameAdapter : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private val gameList: ArrayList<GameModel> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GameAdapter.GameViewHolder {
        return GameViewHolder(
            ItemGameBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(games: List<GameModel>) {
        gameList.clear()
        gameList.addAll(games)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        holder.bind(gameList[position])
    }

    override fun getItemCount(): Int = gameList.size

    inner class GameViewHolder(val binding: ItemGameBinding) :
        RecyclerView.ViewHolder(binding.root) {

        internal fun bind(game: GameModel) {
            binding.tvName.text = game.gameName
            binding.tvEntryReward.text =
                "Entry: ${game.entryTokenCost} | Reward: ${game.rewardTokenValue}"
            binding.root.setOnClickListener { view ->
                view.findNavController().navigate(
                    GameFragmentDirections.actionWebviewGame(
                        game.gameUrl,
                        "",
                        true,
                        game.orientation
                    )
                )
            }
        }
    }
}