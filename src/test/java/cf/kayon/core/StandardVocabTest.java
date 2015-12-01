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

import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static com.github.stefanbirkner.fishbowl.Fishbowl.exceptionThrownBy;
import static org.junit.Assert.*;

public class StandardVocabTest
{

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test
    public void testUuid() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        UUID uuid = UUID.randomUUID();
        StandardVocab vocab = new StandardVocab(context);

        vocab.initializeUuid(uuid);

        assertSame(uuid, vocab.getUuid());

        IllegalStateException ise1 = exceptionThrownBy(() -> vocab.initializeUuid(UUID.randomUUID()), IllegalStateException.class);
        assertEquals("UUID has already been initialized!", ise1.getMessage());
        assertEquals("UUID has already been initialized!", ise1.getLocalizedMessage());
        assertNull(ise1.getCause());

        IllegalStateException ise2 = exceptionThrownBy(() -> vocab.initializeUuid(uuid), IllegalStateException.class);
        assertEquals("UUID has already been initialized!", ise2.getMessage());
        assertEquals("UUID has already been initialized!", ise2.getLocalizedMessage());
        assertNull(ise2.getCause());
    }

    @Test
    public void testTranslationSetterAndGetter() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        StandardVocab vocab = new StandardVocab(context);

        assertEquals(new HashMap<Locale, String>(), vocab.getTranslations());

        Map<Locale, String> map = getSampleMap();

        vocab.setTranslations(map);

        assertSame(map, vocab.getTranslations());
    }

    @Test
    public void testGetTranslation() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        StandardVocab vocab = new StandardVocab(context);
        Map<Locale, String> map = getSampleMap();
        vocab.setTranslations(map);

        assertNull(vocab.getTranslation(new Locale("fr")));
        assertNull(vocab.getTranslation(new Locale("fr", "CA")));
        assertNull(vocab.getTranslation(new Locale("fr", "FR")));

        assertNull(vocab.getTranslation(new Locale("nl")));
        assertNull(vocab.getTranslation(new Locale("nl", "NL")));

        assertEquals("abc abc abc", vocab.getTranslation(new Locale("de")));
        assertEquals("abd abd abd", vocab.getTranslation(new Locale("de", "DE")));
        assertEquals("abc abc abc", vocab.getTranslation(new Locale("de", "CH")));

        assertEquals("NaNNaNNaNNaN", vocab.getTranslation(new Locale("en")));
        assertEquals("def def def", vocab.getTranslation(new Locale("en", "US")));
        assertEquals("jetbrains", vocab.getTranslation(new Locale("en", "CA")));
        assertEquals("NaNNaNNaNNaN", vocab.getTranslation(new Locale("en", "AU")));
    }

    private Map<Locale, String> getSampleMap()
    {
        Map<Locale, String> map = new HashMap<>();
        map.put(new Locale("de"), "abc abc abc");
        map.put(new Locale("de", "DE"), "abd abd abd");
        map.put(new Locale("en"), "NaNNaNNaNNaN");
        map.put(new Locale("en", "US"), "def def def");
        map.put(new Locale("en", "CA"), "jetbrains");
        return map;
    }
}
