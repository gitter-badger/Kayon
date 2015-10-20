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

import cf.kayon.core.Vocab;
import cf.kayon.core.sql.NounSQLFactory;
import com.google.common.collect.Lists;
import javafx.concurrent.Task;

import java.sql.Connection;
import java.util.List;

public class VocabTask extends Task<List<Vocab>>
{
    private final String searchString;

    private final Connection connection;

    public VocabTask(final String searchString, final Connection connection)
    {
        this.searchString = searchString;
        this.connection = connection;
    }

    @Override
    protected List<Vocab> call() throws Exception
    {
        List<Vocab> vocabSet = Lists.newArrayList();
        vocabSet.addAll(NounSQLFactory.queryNouns(connection, searchString));
        return vocabSet;
    }
}
