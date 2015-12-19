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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Queries vocab out of the database with a given search string.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public class QueryTask implements Callable<Void>
{
    @NotNull
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryTask.class);

    /**
     * The context of this class.
     *
     * @since 0.2.0
     */
    @NotNull
    private final KayonContext context;

    /**
     * The search string of this QueryTask.
     *
     * @since 0.0.1
     */
    @NotNull
    private final String searchString;

    /**
     * The {@link BlockingQueue} this task puts its results on.
     *
     * @since 0.2.3
     */
    @NotNull
    private final BlockingQueue<? super Vocab> queue;

    /**
     * The posion object to notify consumers to stop working.
     *
     * @since 0.2.3
     */
    @NotNull
    private final Vocab poison;

    /**
     * Constructs a new QueryTask.
     *
     * @param context      The {@link KayonContext} for this instance.
     * @param searchString The search string. May be the raw user input - both lowercase and uppercase characters are handled appropriately.
     *                     Also, special regex characters are escaped.
     * @throws NullPointerException If any of the arguments is {@code null}.
     * @since 0.2.0
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    public QueryTask(@NotNull final KayonContext context, @NotNull final String searchString, @NotNull final BlockingQueue<? super Vocab> queue, @NotNull Vocab poison)
    {
        checkNotNull(context);
        checkNotNull(searchString);
        checkNotNull(queue);
        checkNotNull(poison);
        this.context = context;
        this.searchString = searchString;
        this.queue = queue;
        this.poison = poison;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof QueryTask)) return false;
        QueryTask queryTask = (QueryTask) o;
        return Objects.equal(context, queryTask.context) &&
               Objects.equal(searchString, queryTask.searchString) &&
               Objects.equal(queue, queryTask.queue) &&
               Objects.equal(poison, queryTask.poison);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(context, searchString, queue, poison);
    }

    /**
     * @since 0.0.1
     */
    @Override
    @Nullable
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_AND_UPPERCASE)
    public Void call() throws SQLException, InterruptedException
    {
        LOGGER.info("Query task started: " + Thread.currentThread());
        try
        {
            context.getNounSQLFactory().queryNouns(searchString, queue);
        } finally
        {
            while (true)
            {
                try
                {
                    queue.put(poison);
                    break;
                } catch (InterruptedException ignored) {}
            }
        }
        // do not catch InterruptedException or SQLException thrown in try { ... } block
        // (they are logged by the executor)
        LOGGER.info("QueryTask terminated normally: " + Thread.currentThread());
        return null;
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
                          .add("queue", queue)
                          .add("poison", poison)
                          .toString();
    }
}
