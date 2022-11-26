package com.codenode.budgetlens.data

/**
 * Category for users, do not add any logic here
 * it goes in the UserCategory class.
 */
data class Category(
    var category_name: String,
    var category_toggle_star: Boolean,
    var parent_category_id: Int?

) {
    constructor() : this("", false, 0)
}