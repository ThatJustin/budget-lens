package com.codenode.budgetlens.adapter

import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Friends
import com.codenode.budgetlens.data.ReceiptSplitItem
import com.codenode.budgetlens.data.UserFriends

class SplitItemListAdapter :
    BaseQuickAdapter<ReceiptSplitItem, BaseViewHolder>(R.layout.split_item) {
    var itemList: List<ReceiptSplitItem> = emptyList()
    lateinit var friendList: MutableList<Friends>
    init {
        addChildClickViewIds(R.id.tv_split)
    }

    override fun convert(helper: BaseViewHolder, item: ReceiptSplitItem) {
        helper.setText(R.id.name, item.item_name)
        helper.setText(R.id.price, "$" + item.item_price)
        val participantsAdapter = SplitItemParticipantsAdapter()
        val recyclerView: RecyclerView = helper.getView(R.id.recyclerview)
        val participants = mutableListOf<Friends>()
        for (id in item.splitList!!)
            for (friend in friendList) {
                if (id == (friend.userId)) {
                    participants.add(friend)
                }
            }

        recyclerView.adapter = participantsAdapter
        participantsAdapter.setNewInstance(participants)

    }
}
