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

package cf.kayon.gui.vocabview.noun;

import cf.kayon.core.KayonContext;
import cf.kayon.core.noun.Noun;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * Thread safety notice
 *
 * All fields are final
 */
public class NounSaveTask extends Task<Void>
{

    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(NounSaveTask.class);
    @NotNull
    private final Noun noun;
    @NotNull
    private final KayonContext context;

    public NounSaveTask(final @NotNull KayonContext context, final @NotNull Noun noun)
    {
        checkNotNull(context);
        checkNotNull(noun);
        this.context = context;
        this.noun = noun;
    }

    public NounSaveTask(final Noun noun)
    {
        this(noun.getContext(), noun);
    }

    @Nullable
    @Override
    protected Void call() throws Exception
    {
        try
        {
            context.getNounSQLFactory().saveNounToDatabase(noun);
        } catch (SQLException e)
        {
            LOGGER.error("SQLException in NounSaveTask!", e);
            throw e;
        }
        return null;
    }

}
