/*
 * Kayon
 * Copyright (C) 2015 Ruben Anders
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cf.kayon.gui;

import cf.kayon.core.noun.Noun;
import cf.kayon.core.sql.NounSQLFactory;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.sql.SQLException;

public class NounSaveTask extends Task<Void>
{
    public NounSaveTask(final Noun noun, final Connection connection)
    {
        this.noun = noun;
        this.connection = connection;
    }

    private final Noun noun;

    private final Connection connection;

    @Override
    protected Void call() throws Exception
    {
        try
        {
            NounSQLFactory.saveNounToDatabase(connection, noun);
        } catch (SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

}
