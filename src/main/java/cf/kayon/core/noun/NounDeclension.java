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

import cf.kayon.core.CaseHandling;
import cf.kayon.core.FormingException;
import cf.kayon.core.Gender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Describes a declension used for declining nouns.
 * <p>
 * These classes are special: You are required to implement a method of exact signature to be a valid NounDeclension, since your NounDeclension must be a singleton.
 * <pre>{@code
 * public class MyNounDeclension implements NounDeclension
 * {
 *     public static MyNounDeclension getInstance()
 *     {
 *         // return the only instance here.
 *     }
 * }
 * }</pre>
 * <p>
 * The implementation could look like this:
 * <pre>{@code
 * public class MyNounDeclension implements NounDeclension
 * {
 *     private static final MyNounDeclension INSTANCE = new MyNounDeclension();
 *     public static MyNounDeclension getInstance()
 *     {
 *         return INSTANCE;
 *     }
 *     private MyNounDeclension() {}
 *     // the implementation here...
 * }
 * }</pre>
 * <p>
 * This is required by method who want to reconstruct your singleton instance by a simple class name. See {@link NounDeclensionUtil#forName(String)}
 * for additional details.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
public interface NounDeclension
{
    /**
     * Returns the primary gender of this NounDeclension. Useful for user interfaces which then can auto-select the default gender.
     *
     * @return The primary gender. May be null if the implementation does not have a primary gender.
     * @since 0.0.1
     */
    @Nullable Gender getPrimaryGender();

    /**
     * Declines a form based on a root word.
     * <p>
     * One should only apply lowercase root words, since the declining logic might depend on special character constellations
     * in the root word. Only applies lowercase endings (see annotation).
     *
     * @param nounForm The noun form.
     * @param gender   The gender.
     * @param rootWord The root word.
     * @return A declined form.
     * @throws FormingException         If the form could not be determined.
     * @throws NullPointerException     If any of the arguments is null.
     * @throws IllegalArgumentException If {@code rootWord} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull String decline(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String rootWord) throws FormingException;

    /**
     * Determines the root word based on a declined form.
     * <p>
     * One should only apply lowercase declined forms, since the underlying logic is only required to detect lowercase endings
     * and character constellations. The determined root word will be lowercased, if the declined form is lowercase as well (see annotation).
     *
     * @param nounForm     The noun form.
     * @param gender       The gender.
     * @param declinedForm The declined form.
     * @return The root word.
     * @throws FormingException         If the root word could not be determined.
     * @throws NullPointerException     If any of the arguments is null.
     * @throws IllegalArgumentException If {@code declinedForm} is {@link String#isEmpty() empty}.
     * @since 0.0.1
     */
    @CaseHandling(CaseHandling.CaseType.LOWERCASE_ONLY)
    @NotNull String determineRootWord(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException;

    /**
     * Returns whether a specified gender is supported by this NounDeclension. If a gender is not allowed, a usage cannot expect declining results to be accurate,
     * they may then result in an arbitrary wrong form.
     * <p>
     * Implementations are allowed to throw an unchecked exception if a method is called on it with a disallowed gender, though the implementations
     * provided in {@link cf.kayon.core.noun.impl} all just return some arbitrary wrong form.
     *
     * @param genderToCheck The gender to check.
     * @return Whether it is allwed or not.
     * @throws NullPointerException If {@code genderToCheck} is {@code null}.
     * @since 0.0.1
     */
    boolean allowsGender(@NotNull Gender genderToCheck);

}
