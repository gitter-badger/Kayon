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

import cf.kayon.core.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public class FormingUtil
{
    @NotNull
    public static String determineRootWord(@NotNull String declinedForm, @NotNull String ending) throws FormingException
    {
        checkNotNull(declinedForm);
        checkNotNull(ending);
        if (ending.isEmpty()) // Nothing to remove
            return declinedForm;

        @Nullable
        String rootWordOrNull = StringUtil.removeVeryLastOrNull(StringUtil.unSpecialString(declinedForm), declinedForm, StringUtil.unSpecialString(ending));
        if (rootWordOrNull == null)
            throw new FormingException();

        return rootWordOrNull;
    }

    @Nullable
    public static String determineRootWordOrNull(@NotNull String declinedForm, @NotNull String ending) throws FormingException
    {
        checkNotNull(declinedForm);
        checkNotNull(ending);
        if (ending.isEmpty()) // Nothing to remove
            return declinedForm;
        return StringUtil.removeVeryLastOrNull(StringUtil.unSpecialString(declinedForm), declinedForm, StringUtil.unSpecialString(ending));
    }
}
