package com.swapnil.contlo.utility.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.swapnil.contlo.R
import com.swapnil.contlo.model.PullRequest

class PullRequestAdapter(private val clickListener: ClickListener) :
    ListAdapter<PullRequest, PullRequestAdapter.Companion.Holder>(PullRequestDiffUtil()) {

    companion object {
        class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val ivAvatar: ImageView
            private val tvName: TextView
            private val tvCreatedAt: TextView
            private val tvClosedAt: TextView
            private val tvTitleLabel: TextView
            private val tvTitleLabelValue: TextView

            init {
                ivAvatar = itemView.findViewById(R.id.iv_avatar)
                tvName = itemView.findViewById(R.id.tv_label_name_value)
                tvCreatedAt = itemView.findViewById(R.id.tv_label_created_value)
                tvClosedAt = itemView.findViewById(R.id.tv_label_closed_value)
                val includedView = itemView.findViewById<View>(R.id.include_title)
                tvTitleLabel = includedView.findViewById(R.id.tv_label)
                tvTitleLabelValue = includedView.findViewById(R.id.tv_label_value)

                tvTitleLabel.text = itemView.context.getString(R.string.title)
            }

            fun setData(pullRequest: PullRequest) {
                tvName.text = pullRequest.user.userName
                tvCreatedAt.text = pullRequest.createdAt
                tvClosedAt.text = pullRequest.closedAt
                tvTitleLabelValue.text = pullRequest.title
                loadImage(pullRequest.user.avatarUrl)
            }

            private fun loadImage(url: String) {
                Glide.with(itemView.context)
                    .load(url)
                    .into(ivAvatar)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pr_item, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val pullRequest = getItem(position)
        holder.itemView.setOnClickListener {
            clickListener.onPullRequestClicked(pullRequest.prUrl)
        }
        holder.setData(pullRequest)
    }
}

interface ClickListener {
    fun onPullRequestClicked(url: String)
}
class PullRequestDiffUtil : DiffUtil.ItemCallback<PullRequest>() {
    override fun areItemsTheSame(oldItem: PullRequest, newItem: PullRequest): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PullRequest, newItem: PullRequest): Boolean {
        return oldItem.number == newItem.number
    }

}