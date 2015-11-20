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
import cf.kayon.core.Gender;
import cf.kayon.core.noun.impl.ANounDeclension;
import cf.kayon.core.noun.impl.ENounDeclension;
import cf.kayon.core.noun.impl.ONounDeclension;
import org.junit.Test;

import java.beans.PropertyVetoException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class NounDeepCopyTest
{

    @Test
    public void testDeepCopyNounDeclension() throws PropertyVetoException
    {
        Noun noun = new Noun(ANounDeclension.getInstance(), Gender.FEMININE, "domin");
        Noun copy = noun.copyDeep();

        assertEquals(noun, copy);
        assertNotSame(noun, copy);

        copy.setNounDeclension(ENounDeclension.getInstance());

        assertThat(copy, not(equalTo(noun)));
    }

    @Test
    public void testDeepCopyRootWord() throws PropertyVetoException
    {
        Noun noun = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin");
        Noun copy = noun.copyDeep();

        assertEquals(noun, copy);
        assertNotSame(noun, copy);

        copy.setRootWord("dafdefdifdofduf");

        assertThat(copy, not(equalTo(noun)));
    }

    @Test
    public void testDeepCopyUuid() throws PropertyVetoException
    {
        Noun noun = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin");
        Noun copy = noun.copyDeep();

        assertEquals(noun, copy);
        assertNotSame(noun, copy);

        copy.initializeUuid(UUID.fromString("3cd7bd0a-f3b3-457e-9983-3eeec70190ee")); // No randomness for constant test results

        assertThat(copy, not(equalTo(noun)));
    }

    @Test
    public void testDeepCopyTranslations() throws PropertyVetoException
    {
        Noun noun = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin");
        Noun copy = noun.copyDeep();

        assertEquals(noun, copy);
        assertNotSame(noun, copy);

        copy.getTranslations().put(Locale.ENGLISH, "lord");

        assertThat(copy, not(equalTo(noun)));
    }

    @Test
    public void testDeepCopyGender() throws PropertyVetoException
    {
        Noun noun = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin");
        Noun copy = noun.copyDeep();

        assertEquals(noun, copy);
        assertNotSame(noun, copy);

        copy.setGender(Gender.FEMININE);

        assertThat(copy, not(equalTo(noun)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeepCopyDeclinedForms() throws Exception
    {
        Noun noun = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin");
        Noun copy = noun.copyDeep();

        assertEquals(noun, copy);
        assertNotSame(noun, copy);

        Field field = Noun.class.getDeclaredField("declinedForms");
        field.setAccessible(true);
        Map<NounForm, String> declinedForms = (Map<NounForm, String>) field.get(copy);
        declinedForms.put(NounForm.of(Case.ACCUSATIVE, Count.PLURAL), "326e41bfea34a5");

        assertThat(copy, not(equalTo(noun)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeepCopyDefinedForms() throws Exception
    {
        Noun noun = new Noun(ONounDeclension.getInstance(), Gender.MASCULINE, "domin");
        Noun copy = noun.copyDeep();

        assertEquals(noun, copy);
        assertNotSame(noun, copy);

        Field field = Noun.class.getDeclaredField("definedForms");
        field.setAccessible(true);
        Map<NounForm, String> definedForms = (Map<NounForm, String>) field.get(copy);
        definedForms.put(NounForm.of(Case.ACCUSATIVE, Count.PLURAL), "abcuiahwrugwe");

        assertThat(copy, not(equalTo(noun)));
    }


}
