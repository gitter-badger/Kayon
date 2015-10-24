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

package cf.kayon.core.adjective;

import cf.kayon.core.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Describes an declension for declining adjectives.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public interface AdjectiveDeclension
{
    /**
     * Declines a form based on a root word.
     * <p>
     * One should only apply lowercase root words, since the declining logic might depend on special character constellations
     * in the root word. Only applies lowercase endings (see annotation).
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @param rootWord         The root word to base form generation on.
     * @return A declined form.
     * @throws FormingException         If the form could not be determined.
     * @throws NullPointerException     If any of the arguments is null.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    String decline(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String rootWord)
            throws FormingException;

    /**
     * Determines the root word based on a declined form.
     * <p>
     * One should only apply lowercase declined forms, since the underlying logic is only required to detect lowercase endings
     * and character constellations. The determined root word will be lowercased, if the declined form is lowercase as well (see annotation).
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @param declinedForm     The declined form to base root word generation on.
     * @return The root word.
     * @throws FormingException         If the root word could not be determined.
     * @throws NullPointerException     If any of the arguments is null.
     * @throws IllegalArgumentException If {@code declinedForm} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull
    String determineRootWord(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender, @NotNull String declinedForm)
            throws FormingException;

    /**
     * Gets all equal forms of the specified form.
     * <p>
     * The resulting form may - for implementation simplicity - return the specified form itself as well.
     * <p>
     * The resulting set may be empty.
     * <p>
     * The resulting set may be immutable.
     *
     * @param comparisonDegree The comparison degree.
     * @param caze             The case.
     * @param count            The count.
     * @param gender           The gender.
     * @return A set of equal forms. {@code null} if there are no equal forms.
     * @since 0.0.1
     */
    @Nullable
    Set<AdjectiveForm> getEqualForms(@NotNull ComparisonDegree comparisonDegree, @NotNull Case caze, @NotNull Count count, @NotNull Gender gender);
}
