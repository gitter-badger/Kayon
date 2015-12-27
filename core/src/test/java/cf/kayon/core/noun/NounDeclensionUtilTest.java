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
import cf.kayon.core.noun.impl.ANounDeclension;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.*;

public class NounDeclensionUtilTest
{
    @Test
    public void testForName() throws Exception
    {
        NounDeclension declension = NounDeclensionUtil.forName("cf.kayon.core.noun.impl.ANounDeclension");
        assertSame(ANounDeclension.getInstance(), declension);

        NounDeclension declension1 = NounDeclensionUtil.forName("cf.kayon.non.existent.class.NonExistentDeclension");
        assertNull(declension1);

        NounDeclension declension2 = NounDeclensionUtil.forName(null);
        assertNull(declension2);

        NounDeclension declension3 = NounDeclensionUtil.forName("");
        assertNull(declension3);
    }

    @Test
    public void testConstructor() throws Exception
    {
        Constructor<NounDeclensionUtil> privateConstructor = NounDeclensionUtil.class.getDeclaredConstructor();
        privateConstructor.setAccessible(true);
        try
        {
            privateConstructor.newInstance();
            fail("Constructor should throw UnsupportedOperationException!");
        } catch (InvocationTargetException e)
        {
            assertThat(e.getTargetException(), instanceOf(UnsupportedOperationException.class));
            assertNull(e.getTargetException().getMessage());
            assertNull(e.getTargetException().getLocalizedMessage());
            assertNull(e.getTargetException().getCause());
        }
    }

    @Test
    public void testEndingsMap() throws Exception
    {
        @NotNull Map<NounForm, String> map1 = NounDeclensionUtil.endingsMap("NomSi", "GenSi", "DatSi", "AccSi", "AblSi", "VocSi",
                                                                            "NomPl", "GenPl", "DatPl", "AccPl", "AblPl", "VocPl");
        assertEquals(12, map1.size());
        for (NounForm nounForm : NounForm.values())
        {
            assertEquals(nounForm.toString(), map1.get(nounForm));
        }

        @NotNull Map<NounForm, String> map2 = NounDeclensionUtil.endingsMap("NomSi", "GenSi", null, "AccSi", null, "VocSi",
                                                                            "NomPl", "GenPl", null, "AccPl", null, "VocPl");

        assertEquals(8, map2.size());
        for (NounForm nounForm : NounForm.values())
        {
            @NotNull Case caze = nounForm.getCase();
            if (caze != Case.DATIVE && caze != Case.ABLATIVE)
                assertEquals(nounForm.toString(), map2.get(nounForm));
            else
                assertFalse(map2.containsKey(nounForm));
        }
    }

    @Test
    public void testPutIfNotNull() throws Exception
    {
        HashMap<String, String> map = new HashMap<>(2);

        NounDeclensionUtil.putIfNotNull(map, "1", null);
        NounDeclensionUtil.putIfNotNull(map, "2", "two");
        NounDeclensionUtil.putIfNotNull(map, "3", "");

        assertEquals(2, map.size());
        assertNull(map.get("1"));
        assertFalse(map.containsKey("1"));
        assertFalse(map.containsValue(null));
        assertEquals("two", map.get("2"));
        assertEquals("", map.get("3"));
    }
}
