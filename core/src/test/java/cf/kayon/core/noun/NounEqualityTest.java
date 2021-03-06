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
import cf.kayon.core.noun.impl.ONounDeclension;
import net.jcip.annotations.Immutable;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

@Immutable
public class NounEqualityTest
{

    public void assertBidirectionalEquals(Object expected, Object actual)
    {
        assertEquals(expected, actual);
        assertEquals(actual, expected);
    }

    public void assertBidirectionalNotEquals(Object unexpected, Object actual)
    {
        assertThat(unexpected, not(equalTo(actual)));
        assertThat(actual, not(equalTo(unexpected)));
    }

    @Test
    public void testUUIDEquality()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        noun2.initializeUuid(UUID.fromString("dd6c6217-323f-471f-84c3-25fb92ccdb76"));

        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    public void testTranslationEquality()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        noun2.getTranslations().put(Locale.ENGLISH, "slave");

        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    public void testNounDeclensionEquality()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        noun2.setNounDeclension(ANounDeclension.getInstance());

        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    public void testGenderEquality()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        noun2.setGender(Gender.FEMININE);

        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    public void testRootWordEquality()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        noun2.setRootWord("domin");

        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    public void testContextEquality()
    {
        KayonContext context1 = TestContextUtil.newTestingContext();
        KayonContext context2 = TestContextUtil.newTestingContext();
        Noun noun1 = new Noun(context1, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context2, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");

        assertNotSame(noun1, noun2);
        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    public void testDefinedFormsEquality()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        noun2.setDefinedForm(NounForm.of(Case.ACCUSATIVE, Count.PLURAL), "heyaheya");

        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testDeclinedFormsEquality() throws Exception
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        Field field = Noun.class.getDeclaredField("declinedForms");
        field.setAccessible(true);
        Map<NounForm, String> map = (Map<NounForm, String>) field.get(noun2);
        map.put(NounForm.of(Case.NOMINATIVE, Count.SINGULAR), "example123");

        assertBidirectionalNotEquals(noun1, noun2);
    }

    @Test
    public void testPropertyChangeSupportEquality()
    {
        KayonContext context = TestContextUtil.newTestingContext();

        Noun noun1 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        Noun noun2 = new Noun(context, ONounDeclension.getInstance(), Gender.MASCULINE, "serv");
        assertBidirectionalEquals(noun1, noun2);
        assertNotSame(noun1, noun2);

        noun2.addPropertyChangeListener(evt -> fail());

        assertBidirectionalEquals(noun1, noun2);
    }

}
