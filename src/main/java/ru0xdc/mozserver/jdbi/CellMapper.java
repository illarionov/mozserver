package ru0xdc.mozserver.jdbi;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import ru0xdc.mozserver.model.Cell;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CellMapper implements ResultSetMapper<Cell>
{
    public Cell map(int index, ResultSet r, StatementContext ctx) throws SQLException
    {
        return new Cell(
                r.getString("radio"),
                r.getInt("mcc"),
                r.getInt("mnc"),
                r.getInt("lac"),
                r.getInt("cid"),
                r.getInt("psc"));
    }
}