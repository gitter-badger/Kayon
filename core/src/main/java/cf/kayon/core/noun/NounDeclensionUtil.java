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
import cf.kayon.core.util.Tested;
import com.google.common.collect.ImmutableMap;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Provides static utilities for {@link NounDeclension}s.
 *
 * @author Ruben Anders
 * @since 0.0.1
 */
@Immutable
public class NounDeclensionUtil
{

    /**
     * A private constructor to prevent instantiation of this class.
     *
     * @throws IllegalStateException always
     * @since 0.2.3
     */
    @Tested("cf.kayon.core.noun.NounDeclensionUtilTest.testConstructor")
    private NounDeclensionUtil()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Constructs a {@link java.util.Collections#unmodifiableMap(Map) unmodifiable} map of endings.
     *
     * @param nomSg The nominative singular form.
     * @param genSg The genitive singular form.
     * @param datSg The dative singular form.
     * @param accSg The accusative singular form.
     * @param ablSg The ablative singular form.
     * @param vocSg The vocative singular form.
     * @param nomPl The nominative  plural form.
     * @param genPl The genitive plural form.
     * @param datPl The dative plural form.
     * @param accPl The accusative plural form.
     * @param ablPl The ablative plural form.
     * @param vocPl The vocative plural form.
     * @return A map of endings.
     * @since 0.2.0
     */
    @NotNull
    @Tested("cf.kayon.core.noun.NounDeclensionUtilTest.testEndingsMap")
    public static Map<NounForm, String> endingsMap(
            @Nullable String nomSg, @Nullable String genSg, @Nullable String datSg, @Nullable String accSg, @Nullable String ablSg, @Nullable String vocSg,
            @Nullable String nomPl, @Nullable String genPl, @Nullable String datPl, @Nullable String accPl, @Nullable String ablPl, @Nullable String vocPl)
    {
        @NotNull
        Map<NounForm, String> map = new HashMap<>(12);

        putIfNotNull(map, NounForm.of(Case.NOMINATIVE, Count.SINGULAR), nomSg);
        putIfNotNull(map, NounForm.of(Case.GENITIVE, Count.SINGULAR), genSg);
        putIfNotNull(map, NounForm.of(Case.DATIVE, Count.SINGULAR), datSg);
        putIfNotNull(map, NounForm.of(Case.ACCUSATIVE, Count.SINGULAR), accSg);
        putIfNotNull(map, NounForm.of(Case.ABLATIVE, Count.SINGULAR), ablSg);
        putIfNotNull(map, NounForm.of(Case.VOCATIVE, Count.SINGULAR), vocSg);
        putIfNotNull(map, NounForm.of(Case.NOMINATIVE, Count.PLURAL), nomPl);
        putIfNotNull(map, NounForm.of(Case.GENITIVE, Count.PLURAL), genPl);
        putIfNotNull(map, NounForm.of(Case.DATIVE, Count.PLURAL), datPl);
        putIfNotNull(map, NounForm.of(Case.ACCUSATIVE, Count.PLURAL), accPl);
        putIfNotNull(map, NounForm.of(Case.ABLATIVE, Count.PLURAL), ablPl);
        putIfNotNull(map, NounForm.of(Case.VOCATIVE, Count.PLURAL), vocPl);

        return ImmutableMap.copyOf(map);
    }

    /**
     * Puts an entry into a map, if the value is not {@code null}.
     *
     * @param map   The map.
     * @param key   The key.
     * @param value The value.
     * @param <K>   The type of the key.
     * @param <V>   The type of the value.
     * @throws NullPointerException If {@code map} or {@code key} are {@code null}.
     * @since 0.2.0
     */
    @Tested("cf.kayon.core.noun.NounDeclensionUtilTest.testPutIfNotNull")
    public static <K, V> void putIfNotNull(@NotNull Map<K, V> map, @NotNull K key, @Nullable V value)
    {
        checkNotNull(map);
        checkNotNull(key);
        if (value != null)
            map.put(key, value);
    }

    /**
     * Reflectively reconstructs a NounDeclension by invoking its {@code public static NounDeclension getInstance()} method.
     *
     * @param className The name of the class.
     * @return A NounDeclension. {@code null} if the reconstruction was not successful or the class name was {@code null}.
     * @since 0.0.1
     */
    @Nullable
    @Contract("null -> null")
    @Tested("cf.kayon.core.noun.NounDeclensionUtilTest.testForName")
    public static NounDeclension forName(@Nullable @NonNls String className)
    {
        if (className == null)
            return null;
        try
        {
            Class<?> clazz = Class.forName(className);
            Method m = clazz.getMethod("getInstance");
            return (NounDeclension) m.invoke(null);
        } catch (Exception e)
        {
            return null;
        }
    }
}
