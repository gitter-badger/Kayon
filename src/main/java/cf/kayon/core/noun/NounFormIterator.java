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

import cf.kayon.core.Case;
import cf.kayon.core.Count;
import org.apache.commons.collections4.ResettableIterator;
import org.apache.commons.collections4.iterators.ObjectArrayIterator;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;

@Deprecated
public class NounFormIterator implements Iterator<Pair<Boolean, String>>, ResettableIterator<Pair<Boolean, String>>
{
    private final Noun noun;
    private final ObjectArrayIterator<Case> caseIterator = new ObjectArrayIterator<>(Case.values());
    private Count currentCount = Count.SINGULAR;

    public NounFormIterator(Noun noun)
    {
        checkNotNull(noun);
        this.noun = noun;
    }

    @Override
    public boolean hasNext()
    {
        return currentCount == Count.SINGULAR || caseIterator.hasNext();
    }

    /*
     * The returned pair might have a null value instead of a string!
     */
    @Override
    @NotNull
    public Pair<Boolean, String> next() throws NoSuchElementException
    {
        Case caze;
        if (caseIterator.hasNext())
        {
            caze = caseIterator.next();
        } else // Switch to Plural
        {
            if (currentCount == Count.SINGULAR)
            {
                currentCount = Count.PLURAL;
                caseIterator.reset();
                caze = caseIterator.next();
            } else
            {
                throw new NoSuchElementException("Reachend end of iteration!");
            }
        }

        String definedFormOrNull = noun.getDefinedForm(caze, currentCount);
        if (definedFormOrNull != null)
            return new ImmutablePair<>(true, definedFormOrNull);
        String declinedFormOrNull = noun.getDeclinedForm(caze, currentCount);
        return new ImmutablePair<>(false, declinedFormOrNull);
    }

    @Override
    public void reset()
    {
        caseIterator.reset();
        currentCount = Count.SINGULAR;
    }
}
