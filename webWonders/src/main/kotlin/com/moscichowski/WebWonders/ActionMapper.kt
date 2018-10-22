package com.moscichowski.WebWonders

import org.springframework.jdbc.core.RowMapper
import java.sql.ResultSet

data class Action(val id: Int, val action: String) {
}

class ActionMapper: RowMapper<Action> {
    override fun mapRow(rs: ResultSet, rowNum: Int): Action? {
        return Action(rs.getInt("id"), rs.getString("action"))
    }
}