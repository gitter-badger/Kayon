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
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.*;

public class NounFormTest
{

    @Test
    public void testEquals() throws Exception
    {
        Constructor<NounForm> privateConstructor = NounForm.class.getDeclaredConstructor(Case.class, Count.class);
        privateConstructor.setAccessible(true);

        Field privatePropertyName = NounForm.class.getDeclaredField("propertyName");
        privatePropertyName.setAccessible(true);

        int counter = 0;
        for (Count count : Count.values())
            for (Case caze : Case.values())
            {
                NounForm form = privateConstructor.newInstance(caze, count);
                assertEquals(NounForm.of(caze, count), form);
                privatePropertyName.set(form, "testEquality");
                assertEquals(NounForm.of(caze, count), form);
                assertSame(NounForm.of(caze, count), NounForm.values().get(counter++));
            }
    }

    @Test
    public void testToString() throws Exception
    {
        assertEquals("NomSi", NounForm.of(Case.NOMINATIVE, Count.SINGULAR).toString());
        assertEquals("GenSi", NounForm.of(Case.GENITIVE, Count.SINGULAR).toString());
        assertEquals("DatSi", NounForm.of(Case.DATIVE, Count.SINGULAR).toString());
        assertEquals("AccSi", NounForm.of(Case.ACCUSATIVE, Count.SINGULAR).toString());
        assertEquals("AblSi", NounForm.of(Case.ABLATIVE, Count.SINGULAR).toString());
        assertEquals("VocSi", NounForm.of(Case.VOCATIVE, Count.SINGULAR).toString());

        assertEquals("NomPl", NounForm.of(Case.NOMINATIVE, Count.PLURAL).toString());
        assertEquals("GenPl", NounForm.of(Case.GENITIVE, Count.PLURAL).toString());
        assertEquals("DatPl", NounForm.of(Case.DATIVE, Count.PLURAL).toString());
        assertEquals("AccPl", NounForm.of(Case.ACCUSATIVE, Count.PLURAL).toString());
        assertEquals("AblPl", NounForm.of(Case.ABLATIVE, Count.PLURAL).toString());
        assertEquals("VocPl", NounForm.of(Case.VOCATIVE, Count.PLURAL).toString());
    }

    @Test
    public void testValues() throws Exception
    {
        assertEquals(12, NounForm.values().size());
        int i = 0;
        assertEquals(NounForm.of(Case.NOMINATIVE, Count.SINGULAR), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.GENITIVE, Count.SINGULAR), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.DATIVE, Count.SINGULAR), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.ACCUSATIVE, Count.SINGULAR), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.ABLATIVE, Count.SINGULAR), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.VOCATIVE, Count.SINGULAR), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.NOMINATIVE, Count.PLURAL), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.GENITIVE, Count.PLURAL), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.DATIVE, Count.PLURAL), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.ACCUSATIVE, Count.PLURAL), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.ABLATIVE, Count.PLURAL), NounForm.values().get(i++));
        assertEquals(NounForm.of(Case.VOCATIVE, Count.PLURAL), NounForm.values().get(i++));
        assertEquals(12, i); // make sure no value is forgotten
    }

    @Test
    public void testGetCase() throws Exception
    {
        assertEquals(Case.NOMINATIVE, NounForm.of(Case.NOMINATIVE, Count.SINGULAR).getCase());
        assertEquals(Case.GENITIVE, NounForm.of(Case.GENITIVE, Count.SINGULAR).getCase());
        assertEquals(Case.DATIVE, NounForm.of(Case.DATIVE, Count.SINGULAR).getCase());
        assertEquals(Case.ACCUSATIVE, NounForm.of(Case.ACCUSATIVE, Count.SINGULAR).getCase());
        assertEquals(Case.ABLATIVE, NounForm.of(Case.ABLATIVE, Count.SINGULAR).getCase());
        assertEquals(Case.VOCATIVE, NounForm.of(Case.VOCATIVE, Count.SINGULAR).getCase());
        assertEquals(Case.NOMINATIVE, NounForm.of(Case.NOMINATIVE, Count.PLURAL).getCase());
        assertEquals(Case.GENITIVE, NounForm.of(Case.GENITIVE, Count.PLURAL).getCase());
        assertEquals(Case.DATIVE, NounForm.of(Case.DATIVE, Count.PLURAL).getCase());
        assertEquals(Case.ACCUSATIVE, NounForm.of(Case.ACCUSATIVE, Count.PLURAL).getCase());
        assertEquals(Case.ABLATIVE, NounForm.of(Case.ABLATIVE, Count.PLURAL).getCase());
        assertEquals(Case.VOCATIVE, NounForm.of(Case.VOCATIVE, Count.PLURAL).getCase());
    }

    @Test
    public void testGetCount() throws Exception
    {
        assertEquals(Count.SINGULAR, NounForm.of(Case.NOMINATIVE, Count.SINGULAR).getCount());
        assertEquals(Count.SINGULAR, NounForm.of(Case.GENITIVE, Count.SINGULAR).getCount());
        assertEquals(Count.SINGULAR, NounForm.of(Case.DATIVE, Count.SINGULAR).getCount());
        assertEquals(Count.SINGULAR, NounForm.of(Case.ACCUSATIVE, Count.SINGULAR).getCount());
        assertEquals(Count.SINGULAR, NounForm.of(Case.ABLATIVE, Count.SINGULAR).getCount());
        assertEquals(Count.SINGULAR, NounForm.of(Case.VOCATIVE, Count.SINGULAR).getCount());
        assertEquals(Count.PLURAL, NounForm.of(Case.NOMINATIVE, Count.PLURAL).getCount());
        assertEquals(Count.PLURAL, NounForm.of(Case.GENITIVE, Count.PLURAL).getCount());
        assertEquals(Count.PLURAL, NounForm.of(Case.DATIVE, Count.PLURAL).getCount());
        assertEquals(Count.PLURAL, NounForm.of(Case.ACCUSATIVE, Count.PLURAL).getCount());
        assertEquals(Count.PLURAL, NounForm.of(Case.ABLATIVE, Count.PLURAL).getCount());
        assertEquals(Count.PLURAL, NounForm.of(Case.VOCATIVE, Count.PLURAL).getCount());
    }

    @Test
    public void testGetPropertyName() throws Exception
    {
        assertEquals("NOMINATIVE_SINGULAR_sample", NounForm.of(Case.NOMINATIVE, Count.SINGULAR).getPropertyName("sample"));
        assertEquals("GENITIVE_SINGULAR_sample", NounForm.of(Case.GENITIVE, Count.SINGULAR).getPropertyName("sample"));
        assertEquals("DATIVE_SINGULAR_sample", NounForm.of(Case.DATIVE, Count.SINGULAR).getPropertyName("sample"));
        assertEquals("ACCUSATIVE_SINGULAR_sample", NounForm.of(Case.ACCUSATIVE, Count.SINGULAR).getPropertyName("sample"));
        assertEquals("ABLATIVE_SINGULAR_sample", NounForm.of(Case.ABLATIVE, Count.SINGULAR).getPropertyName("sample"));
        assertEquals("VOCATIVE_SINGULAR_sample", NounForm.of(Case.VOCATIVE, Count.SINGULAR).getPropertyName("sample"));
        assertEquals("NOMINATIVE_PLURAL_sample", NounForm.of(Case.NOMINATIVE, Count.PLURAL).getPropertyName("sample"));
        assertEquals("GENITIVE_PLURAL_sample", NounForm.of(Case.GENITIVE, Count.PLURAL).getPropertyName("sample"));
        assertEquals("DATIVE_PLURAL_sample", NounForm.of(Case.DATIVE, Count.PLURAL).getPropertyName("sample"));
        assertEquals("ACCUSATIVE_PLURAL_sample", NounForm.of(Case.ACCUSATIVE, Count.PLURAL).getPropertyName("sample"));
        assertEquals("ABLATIVE_PLURAL_sample", NounForm.of(Case.ABLATIVE, Count.PLURAL).getPropertyName("sample"));
        assertEquals("VOCATIVE_PLURAL_sample", NounForm.of(Case.VOCATIVE, Count.PLURAL).getPropertyName("sample"));

        NullPointerException npe = exceptionThrownBy(() -> NounForm.of(Case.NOMINATIVE, Count.SINGULAR).getPropertyName(null),
                                                     NullPointerException.class);
        assertNull(npe.getCause());
        assertNull(npe.getMessage());
        assertNull(npe.getLocalizedMessage());

        IllegalArgumentException iae = exceptionThrownBy(() -> NounForm.of(Case.NOMINATIVE, Count.SINGULAR).getPropertyName(""),
                                                         IllegalArgumentException.class);
        assertNull(iae.getCause());
        assertEquals("Empty string parameter", iae.getMessage());
        assertEquals("Empty string parameter", iae.getLocalizedMessage());
    }

    @Test
    public void testOf() throws Exception
    {
        for (Count count : Count.values())
            for (Case caze : Case.values())
            {
                @NotNull NounForm nounForm = NounForm.of(caze, count);
                assertEquals(caze, nounForm.getCase());
                assertEquals(count, nounForm.getCount());
                assertEquals(caze + "_" + count + "_example", nounForm.getPropertyName("example"));
                assertSame(nounForm, NounForm.of(caze, count)); // call method again, identity-same object
            }
    }
}
