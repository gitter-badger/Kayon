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

package cf.kayon.core;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class KayonTestingUtil
{

    @NotNull
    public static <T> Matcher<Collection<T>> containsOnly(Collection<T> elements)
    {
        return new ContainsOnly<>(elements);
    }

    private static class ContainsOnly<T> extends TypeSafeMatcher<Collection<T>>
    {

        private Collection<T> collectionToCheckAgainst;

        public ContainsOnly(Collection<T> elements)
        {
            this.collectionToCheckAgainst = elements;
        }

        @Override
        protected boolean matchesSafely(Collection<T> item)
        {
            return item.size() == collectionToCheckAgainst.size() && item.containsAll(collectionToCheckAgainst);
        }

        @Override
        public void describeTo(Description description)
        {
            description.appendValue(collectionToCheckAgainst);
        }
    }
}
