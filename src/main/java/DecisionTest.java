import org.junit.Test;

import static org.junit.Assert.*;

public class DecisionTest {

    @Test
    public void noBracketsTest() {
        assertEquals("1 + 2 + 3 = 6", new Decision("1 2 3 = 6").toString());
        assertEquals("1 + 2 - 1 = 2", new Decision("1 2 1 = 2").toString());
        assertEquals("15 * 3 - 2 + 1 = 44", new Decision("15 3 2 1 = 44").toString());
        assertEquals("3 * 3 - 8 = 1", new Decision("3 3 8 = 1").toString());
        assertEquals("3 * 3 + 8 = 17", new Decision("3 3 8 = 17").toString());
        assertEquals("1 + 1 - 1 * 1 = 1", new Decision("1 1 1 1 = 1").toString());
    }

    @Test
    //Умножение имеет приоритет выше сложения
    public void multiplicationTest() {
        assertEquals("1 + 2 * 3 * 7 + 1 = 44", new Decision("1 2 3 7 1 = 44").toString());
        assertEquals("3 * 3 * 3 + 2 * 2 * 2 = 35", new Decision("3 3 3 2 2 2 = 35").toString());
        assertEquals("3 * 3 + 3 * 2 + 2 + 2 = 19", new Decision("3 3 3 2 2 2 = 19").toString());
    }

    @Test
    public void withBracketsTest() {
        assertEquals("1 + (5 + 5) * 4 = 41", new Decision("1 (5 5) 4 = 41").toString());
        assertEquals("1 * (7 - 6) = 1", new Decision("1 (7 6) = 1").toString());
        assertEquals("(1 - 7) - (1 - 10) = 3", new Decision("(1 7) (1 10) = 3").toString());
        assertEquals("(7 - 7) * (12345 + 990) = 0", new Decision("(7 7) (12345 990) = 0").toString());
        assertEquals("(8 + 0) * 8 - 12 + 3 + 7 * (3 * 32 - 2 + 41) = 1000",
                new Decision("(8 0) 8 12 3 7 (3 32 2 41) = 1000").toString());
    }
    @Test
    public void withBracketsTes1() {
        assertEquals("", new Decision("  = 1").toString());
    }

    @Test
    public void SyntaxTest() {
        assertEquals("3 = 3", new Decision("3 = 3").toString());
        assertEquals("3 = 3", new Decision("(3) = 3").toString());
        assertTrue(Decision.validateString("1 3 (5 8) (4 3 7 172316) = 487"));
        assertFalse(Decision.validateString("() = 9"));
        assertFalse(Decision.validateString("1 (7 4 5 (76 9)) = 9"));
        assertFalse(Decision.validateString("(18) = (9 2)"));
        assertFalse(Decision.validateString("1 6  9 = 9"));
        assertFalse(Decision.validateString("1 2 == 3"));
        assertFalse(Decision.validateString("(1 8 = 9"));
        assertFalse(Decision.validateString("1 8) = 9"));
        assertFalse(Decision.validateString("(1 3)(3 1) = 9"));
        assertFalse(Decision.validateString("1= 1"));
        assertFalse(Decision.validateString("1 =1"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void WrongSyntaxTest100() {
        assertEquals("", new Decision("1000 2 = v").toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void WrongSyntaxTest1() {
        assertEquals("Error code 3: Unexpected end of math-case", new Decision("1 4").toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void WrongSyntaxTest2() {
        assertEquals("Error code 3: Unexpected end of math-case", new Decision("1 4 = ").toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void WrongSyntaxTest3() {
        assertEquals("Error code 2: Result of math-case should be a number, near \" = -\"",
                new Decision("1 4 = -").toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void WrongSyntaxTest4() {
        assertEquals("Error code -3: Number expected, near \"*\"", new Decision("*").toString());
    }
    @Test(expected = IllegalArgumentException.class)
    public void WrongSyntaxTest5() {
        assertEquals("Error code 1 : Input string can't be empty", new Decision("").toString());
    }



    @Test
    public void withNoSolution() {
        assertNull(new Decision("2 3 = 0").noBrackets());
        assertNull(new Decision("1 = 0").noBrackets());
        assertNull(new Decision("(8 0) 8 12 3 7 (3 32 2 41) = 1012").withBrackets());
    }
}
