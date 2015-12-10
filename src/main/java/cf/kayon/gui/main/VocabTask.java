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

package cf.kayon.gui.main;

import cf.kayon.core.CaseHandling;
import cf.kayon.core.KayonContext;
import cf.kayon.core.Vocab;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import javafx.concurrent.Task;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Queries vocab out of the database with a given search string.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class VocabTask extends Task<List<Vocab>>
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(VocabTask.class);

    /**
     * The context of this class.
     *
     * @since 0.2.0
     */
    @NotNull
    private final KayonContext context;

    /**
     * The search string of this VocabTask.
     *
     * @since 0.0.1
     */
    @NotNull
    private final String searchString;

    /**
     * Constructs a new VocabTask.
     *
     * @param context      The {@link KayonContext} for this instance.
     * @param searchString The search string. May be the raw user input - both lowercase and uppercase characters are handled appropriately.
     *                     Also, special regex characters are escaped.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.2.0
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    public VocabTask(@NotNull final KayonContext context, @NotNull final String searchString)
    {
        checkNotNull(context);
        checkNotNull(searchString);
        this.context = context;
        this.searchString = Pattern.quote(searchString.toLowerCase());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Even though this method is annotated as {@link cf.kayon.core.CaseHandling.CaseType#LOWERCASE_ONLY}, the {@link VocabTask#VocabTask(KayonContext, String) constructor
     * of this class} handles both uppercase and lowercase characters by converting the string it was constructed with to lowercase.
     *
     * @since 0.0.1
     */
    @Override
    @NotNull
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    protected List<Vocab> call() throws Exception
    {
        @NotNull
        final List<Vocab> collectedResults = Collections.synchronizedList(new ArrayList<>());
        try
        {
            collectedResults.addAll(context.getNounSQLFactory().queryNouns(searchString));
            // Add more types here
        } catch (SQLException e)
        {
            LOGGER.error("SQLException in VocabTask!", e);
            throw e;
        }
        return collectedResults;
    }

    /**
     * @since 0.2.3
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof VocabTask)) return false;
        VocabTask vocabTask = (VocabTask) o;
        return Objects.equal(context, vocabTask.context) &&
               Objects.equal(searchString, vocabTask.searchString);
    }

    /**
     * @since 0.2.3
     */
    @Override
    public int hashCode()
    {
        return Objects.hashCode(context, searchString);
    }

    /**
     * @since 0.2.3
     */
    @Override
    public String toString()
    {
        return MoreObjects.toStringHelper(this)
                          .add("context", context)
                          .add("searchString", searchString)
                          .toString();
    }
}
