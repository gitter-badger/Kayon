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

import cf.kayon.core.*;
import cf.kayon.core.noun.impl.ANounDeclension;
import net.jcip.annotations.Immutable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import static org.junit.Assert.*;

@Immutable
public class NounTest
{

    @NotNull
    public static final NounDeclension STATIC_NOUN_DECLENSION = new NounDeclension()
    {
        @Override
        public @Nullable Gender getPrimaryGender()
        {
            return Gender.FEMININE;
        }

        @Override
        public @NotNull String decline(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
        {
            assertNotNull(nounForm);
            assertNotNull(gender);
            assertNotNull(rootWord);
            return "declined";
        }

        @Override
        public @NotNull String determineRootWord(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
        {
            assertNotNull(nounForm);
            assertNotNull(gender);
            assertNotNull(declinedForm);
            return "root";
        }

        @Override
        public boolean allowsGender(@NotNull Gender genderToCheck)
        {
            assertNotNull(genderToCheck);
            return genderToCheck == Gender.FEMININE;
        }
    };

    @Test
    public void testSetGetDefinedForm() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        Noun noun = new Noun(context, ANounDeclension.getInstance(), Gender.FEMININE, "ancill");
        noun.setDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL), "genplset");

        assertEquals("genplset", noun.getDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL)));
    }

    @Test
    public void testRemoveDefinedForm() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        Noun noun = new Noun(context, ANounDeclension.getInstance(), Gender.FEMININE, "ancill");
        noun.setDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL), "genplset");
        noun.removeDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL));

        assertNull(noun.getDefinedForm(NounForm.of(Case.GENITIVE, Count.PLURAL)));
    }

    @Test
    public void testGetDeclinedForm() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        Noun noun = new Noun(context, new NounDeclension()
        {
            @Override
            public @Nullable Gender getPrimaryGender()
            {
                return Gender.FEMININE;
            }

            @Override
            public @NotNull String decline(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String rootWord) throws FormingException
            {
                assertNotNull(nounForm);
                assertNotNull(gender);
                assertNotNull(rootWord);
                return "declined";
            }

            @Override
            public @NotNull String determineRootWord(@NotNull NounForm nounForm, @NotNull Gender gender, @NotNull String declinedForm) throws FormingException
            {
                assertNotNull(nounForm);
                assertNotNull(gender);
                assertNotNull(declinedForm);
                return "root";
            }

            @Override
            public boolean allowsGender(@NotNull Gender genderToCheck)
            {
                assertNotNull(genderToCheck);
                return genderToCheck == Gender.FEMININE;
            }
        }, Gender.FEMININE, "ancill");

        NounForm.values().forEach(f -> assertEquals("declined", noun.getDeclinedForm(f)));
    }

    @Test
    public void testGetForm() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        Noun noun = new Noun(context, STATIC_NOUN_DECLENSION, Gender.FEMININE, "ancill");

        NounForm.values().forEach(f -> assertEquals("declined", noun.getForm(f)));

        noun.setDefinedForm(NounForm.of(Case.GENITIVE, Count.SINGULAR), "gensgdef");
        @NotNull NounForm nf = NounForm.of(Case.GENITIVE, Count.SINGULAR);

        NounForm.values().forEach(f -> assertEquals(f == nf ? "gensgdef" : "declined", noun.getForm(f)));
    }

    @Test
    public void testSetGetGender() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        Noun noun = new Noun(context, STATIC_NOUN_DECLENSION, Gender.FEMININE, "ancill");
        assertEquals(Gender.FEMININE, noun.getGender());

        noun.setGender(Gender.NEUTER);

        assertEquals(Gender.NEUTER, noun.getGender());
    }

    @Test
    public void testSetGetRootWord() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        Noun noun = new Noun(context, STATIC_NOUN_DECLENSION, Gender.FEMININE, "ancill");
        assertEquals("ancill", noun.getRootWord());

        noun.setRootWord("serv");

        assertEquals("serv", noun.getRootWord());
    }

    @Test
    public void testSetGetNounDeclension() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();
        Noun noun = new Noun(context, STATIC_NOUN_DECLENSION, Gender.FEMININE, "ancill");
        assertSame(STATIC_NOUN_DECLENSION, noun.getNounDeclension());

        noun.setNounDeclension(ANounDeclension.getInstance());

        assertSame(ANounDeclension.getInstance(), noun.getNounDeclension());
    }
}
