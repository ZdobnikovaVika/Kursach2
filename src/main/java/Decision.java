
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Данный класс проверяет правильность введенных данных, а так же содержит методы, которые правильно расставляют арифметические
 * знаки, в рамках задачи.
 *
 */

public class Decision {
    private String math; //исходная строка
    private int[] numbers;
    private int expectedResult;//результат
    private int signsCount;//количество знаков
    private boolean[] signsScoped;
    private boolean containsBrackets;
    private char[] solution;
    private boolean solved = false;//решено; нерешено

    public Decision(String mathCase) throws IllegalArgumentException {
        math = mathCase;
        if (!Decision.validateString(mathCase)) {
            System.err.println("Error code -1 : Не правильный формат примера");
            System.exit(-1);
            throw new IllegalArgumentException();
        }
        Matcher m = Pattern.compile("\\d+").matcher(math);
        LinkedList<Integer> n = new LinkedList<>();
        while (m.find())
            n.add(Integer.parseInt(m.group()));
        expectedResult = n.pollLast();
        numbers = n.stream().mapToInt(i -> i).toArray();
        signsCount = numbers.length - 1;
        signsScoped = searchBrackets();
        compareSum();
    }

    /**
     * проверяет строку
     *
     */

    public static boolean validateString(String str) {
        if (str.equals("")) {
            System.err.println("Error code 1 : Input string can't be empty");
            System.exit(1);
        }
        else {
            int length = str.length();
            boolean scoped = false;
            State state = State.EXPECT_VALUE;
            for (int i = 0; i < length; i++) {
                char c = str.charAt(i);
                switch (state) {
                    case EXPECT_VALUE:
                        if (c >= '0' && c <= '9') {
                            state = State.EXPECT_VALUE_OR_SPACE;
                        } else if (c == '(') {
                            if (scoped) {
                                System.err.println("Error code -2: Unexpected \"(\", already in scopes, near \""
                                        + str.substring(Math.max(0, i - 3), i + 1) + "\"");
                                System.exit(-2);
                                return false;
                            } else
                                scoped = true;
                        }
                        else {
                            System.err.println("Error code -3: Number expected, near \""
                                    + str.substring(Math.max(0, i - 3) ,i + 1) + "\"");
                            System.exit(-3);
                            return false;
                        }
                        break;
                    case EXPECT_VALUE_OR_SPACE:
                        if (c == ' ') {
                            state = State.EXPECT_VALUE_OR_EQUAL;
                        } else if (c == ')') {
                            if (!scoped) {
                                System.err.println("Error code -4: Unexpected \")\", not in scopes, near \""
                                        + str.substring(Math.max(0, i - 3), i + 1) + "\"");
                                System.exit(-4);
                                return false;

                            }
                            else {
                                scoped = false;
                                state = State.EXPECT_SPACE;
                            }
                        } else if (c < '0' || c > '9') {
                            System.err.println("Error code -5: Number or space expected, near \""
                                    + str.substring(Math.max(0, i - 3) ,i + 1) + "\"");
                            System.exit(-5);
                            return false;
                        }
                        break;
                    case EXPECT_VALUE_OR_EQUAL:
                        if (c == '=') {
                            if (i < length - 1 && str.charAt(i + 1) == ' ') {
                                i++;
                                state = State.EXPECT_RESULT;
                            }  else {
                                System.err.println("Error code -6: Space expected after \"=\" expected, near \""
                                        + str.substring(Math.max(0, i - 3) ,i + 1) + "\"");
                                System.exit(-5);
                                return false;
                            }
                        } else if (c == '(') {
                            if (scoped) {
                                System.err.println("Error code -2: Unexpected \"(\", already in scopes, near \""
                                        + str.substring(Math.max(0, i - 3), i + 1) + "\"");
                                System.exit(-2);
                                return false;
                            } else
                                scoped = true;
                        } else if (c < '0' || c > '9') {
                            System.err.println("Error code -8: Number or \"=\" expected, near \""
                                    + str.substring(Math.max(0, i - 3) ,i + 1) + "\"");
                            System.exit(-8);
                            return false;
                        } else {
                            state = State.EXPECT_VALUE_OR_SPACE;
                        }
                        break;
                    case EXPECT_SPACE:
                        if (c == ' ') {
                            state = State.EXPECT_VALUE_OR_EQUAL;
                        } else {
                            System.err.println("\"Error code -9: Space expected, near \""
                                    + str.substring(Math.max(0, i - 3) ,i) + "\"");
                            return false;
                        }
                        break;
                    case EXPECT_RESULT:
                    case EXPECT_RESULT_CONTINUE:
                        if (c >= '0' && c <= '9') {
                            state = State.EXPECT_RESULT_CONTINUE;
                        } else {
                            System.err.println("Error code 2: Result of math-case should be a number, near \""
                                    + str.substring(Math.max(0, i - 3) ,i + 1) + "\"");
                            System.exit(2);
                            return false;
                        }
                        break;
                }
            }
            if (state != State.EXPECT_RESULT_CONTINUE) {
                System.err.println("Error code 3: Unexpected end of math-case");
                System.exit(3);
                return false;
            } else if (scoped) {
                System.err.println("Error code 4: Scopes wasn't closed somewhere");
                System.exit(4);
                return false;
            }
            return true;
        }
        return false;
    }

    public void compareSum() {
        if (!(Arrays.stream(numbers).sum() == expectedResult)) {
            solution = (containsBrackets) ? withBrackets() : noBrackets();
            if (solution == null)
                System.err.println("Невозможно подобрать знаки");
            else
                solved = true;
        } else {
            solution = new char[signsCount];
            solved = true;
            for (int i = 0; i < signsCount; i++)
                solution[i] = '+';
        }
    }
    public char[] noBrackets() {
        return Solve(new char[signsCount], 0, false);
    }
    public char[] withBrackets() {
        return Solve(new char[signsCount], 0, true);
    }

    //решение
    private char[] Solve(char[] signsArr, int signIndex, boolean withBrackets) {
        char[] signs = {'+', '-', '*'};
        if (signIndex < signsArr.length) {
            for (int i = 0; i < 3; i++) {
                signsArr[signIndex] = signs[i];
                if (signIndex != signsCount - 1) {
                    char[] result = Solve(signsArr, signIndex + 1, withBrackets);
                    if (result != null)
                        return signsArr;
                }
                else
                if (((withBrackets) ? this.EvaluateScoped(signsArr, numbers) :
                    this.Evaluate(signsArr, numbers)) == expectedResult)
                    return signsArr;
            }
        }
        return null;
    }

    //поиск скобок
    public boolean[] searchBrackets() {
        boolean[] signs = new boolean[signsCount];
        boolean scoped = false;
        int signsPassed = 0;
        for (int i = 0; i < math.indexOf("=") && signsPassed < signsCount; i++) {
            char c = math.charAt(i);
            if (c == '(') {
                scoped = true;
                this.containsBrackets = true;
            } else if (c == ')')
                scoped = false;
            else if (c == ' ') {
                signs[signsPassed] = scoped;
                signsPassed++;
            }
        }
        return signs;
    }

    // считает пример с расставленными знаками
    private int Evaluate(char[] signs, int[] values) {
        boolean sumOperation = (signs[0] != '*');
        int sign = 1;
        int accumulator = sumOperation ? values[0] : 0;
        int accumulator2 = (sumOperation) ? 0 : values[0];
        for (int i = 0; i < signs.length; i++) {
            if (signs[i] != '*') {
                if (!sumOperation) {
                    accumulator += accumulator2;
                    accumulator2 = 0;
                    sumOperation = true;
                }
                sign = (signs[i] == '+') ? 1 : -1;
                accumulator += values[i + 1] * sign;
            } else {
                if (sumOperation) {
                    accumulator -= accumulator2 = values[i] * sign;
                    sumOperation = false;
                }
                accumulator2 *= values[i+1];
            }
        }
        return accumulator + accumulator2;
    }

    // считает пример с расставленными знаками и скобками
    private int EvaluateScoped(char[] signs, int[] values) {
        List<Character> signsAccumulated = new ArrayList<>();
        List<Integer> valuesAccumulated = new ArrayList<>();
        char[] genericChars;
        for (int i = 0; i < signs.length; i++) {
            if (!signsScoped[i]) {
                signsAccumulated.add(signs[i]);
                valuesAccumulated.add(values[i]);
            } else {
                int j;
                for (j = i; j < signs.length; j++)
                    if (!signsScoped[j]) break;
                valuesAccumulated.add(Evaluate(Arrays.copyOfRange(signs, i, j), Arrays.copyOfRange(values, i, j + 1)));
                if (j < signs.length)
                    signsAccumulated.add(signs[j]);
                i = j;
            }
        }
        if (!signsScoped[signs.length - 1])
            valuesAccumulated.add(values[signs.length]);
        genericChars = new char[signsAccumulated.size()];
        for (int i = 0; i < genericChars.length; i++)
            genericChars[i] = signsAccumulated.get(i);
        return Evaluate(genericChars ,valuesAccumulated.stream().mapToInt(i -> i).toArray());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (solution != null || solved) {
            boolean scoped = false;
            for (int i = 0; i < signsCount; i++) {
                if (signsScoped[i] && !scoped) {
                    str.append("(").append(numbers[i]);
                    scoped = true;
                } else if (!signsScoped[i] && scoped) {
                    str.append(numbers[i]).append(")");
                    scoped = false;
                } else {
                    str.append(numbers[i]);
                }
                str.append(" ").append(solution[i]).append(" ");
            }
            str.append(numbers[signsCount]);
            if (signsCount > 0 && signsScoped[signsCount - 1])
                str.append(")");
            str.append(" = ").append(expectedResult);
        }
        return str.toString();
    }

    public void print() {
        System.out.println(this.toString());
    }
    private enum State {
        EXPECT_VALUE,
        EXPECT_VALUE_OR_SPACE,
        EXPECT_VALUE_OR_EQUAL,
        EXPECT_SPACE,
        EXPECT_RESULT,
        EXPECT_RESULT_CONTINUE
    }
}
