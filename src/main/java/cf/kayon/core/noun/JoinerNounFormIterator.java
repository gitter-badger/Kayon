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

package cf.kayon.core.noun;

import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@Deprecated
// Abandoned
public class JoinerNounFormIterator implements Iterator<String>, ResettableIterator<String>
{
    private final NounFormIterator delegate;

    public JoinerNounFormIterator(Noun noun)
    {
        this(new NounFormIterator(noun));
    }

    public JoinerNounFormIterator(NounFormIterator delegate)
    {
        this.delegate = delegate;
    }

    @Override
    public void reset()
    {
        delegate.reset();
    }

    @Override
    public boolean hasNext()
    {
        return delegate.hasNext();
    }

    @Override
    @NotNull
    public String next()
    {
        Pair<Boolean, String> returnedPair = delegate.next();
        String stringOrNull = returnedPair.getRight();
        return stringOrNull == null ? "" : stringOrNull;
    }
}
