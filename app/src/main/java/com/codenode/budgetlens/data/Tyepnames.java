package com.codenode.budgetlens.data;

import java.io.Serializable;

public class Tyepnames implements Serializable {

    /**
     * id : 8
     * category_name : Food
     * category_toggle_star : true
     * parent_category_id : null
     * sub_category_list : [{"category_name":"Icecream","category_toggle_star":true}]
     */

    public Integer id;
    public String category_name;
    public Boolean category_toggle_star;
}
