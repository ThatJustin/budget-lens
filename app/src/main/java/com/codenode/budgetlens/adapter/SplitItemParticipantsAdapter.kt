package com.codenode.budgetlens.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.CalendarBean
import com.codenode.budgetlens.data.Friends

class SplitItemParticipantsAdapter : BaseQuickAdapter<Friends, BaseViewHolder>(R.layout.item_user) {

    override fun convert(helper: BaseViewHolder, item: Friends) {
        helper.setText(R.id.name, item.firstName)
    }
}
