package convert.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

public class NumberFormat {

    private static final String[] LOCNAMES = {"ru", "en"};
    private String locale = null;
    private ArrayList<String> scaleUnits = new ArrayList<String>();
    private String[][] tokens = new String[4][20];
    private String[][] endings = new String[6][6];
    private static boolean minus = false;
    private static boolean floatFormat = false;
    static private final int ENDING_LEN = 5;
    private int lastThird = 0;
    private ArrayList<String> results = new ArrayList<String>();

    private void readDirectory(String str[]) {
        int numberLine;
        for (int i = 0; i < 3; i++) {
            numberLine = 0;
            try {
                File file = new File(str[i]);
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_16);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    switch (i) {
                        case 0: {
                            tokens[numberLine++] = (line).split(", ");
                            break;
                        }
                        case 1: {
                            scaleUnits.add(line);
                            break;
                        }
                        case 2: {
                            endings[numberLine++] = ((line).split(","));
                            for (int temp = 0; temp < endings[numberLine - 1].length; temp++) {
                                endings[numberLine - 1][temp] = endings[numberLine - 1][temp].replaceAll(" ", "");
                            }
                            break;
                        }
                    }
                }
                br.close();
            } catch (IOException e) {
                throw new RuntimeException("Не удалось считать файл локализации.\n" + e.getMessage());
            }
        }
    }

    public void loadDirectory() {
        String strings[] = new String[3];
        try {
            switch (locale) {
                case "ru": {
                    strings[0] = (getClass().getResource("/resources/tokens_RU.txt").getPath());
                    strings[1] = (getClass().getResource("/resources/scale_units_RU.txt").getPath());
                    strings[2] = (getClass().getResource("/resources/endings_RU.txt").getPath());
                    break;
                }
                case "en": {
                    strings[0] = (getClass().getResource("/resources/tokens_EN.txt").getPath());
                    strings[1] = (getClass().getResource("/resources/scale_units_EN.txt").getPath());
                    strings[2] = (getClass().getResource("/resources/endings_EN.txt").getPath());
                    break;
                }
            }
            readDirectory(strings);
        } catch (NullPointerException e) {
            throw new RuntimeException("Не найден требуемый ресурс или пакет ресурсов для локализации\n" + e.toString());
        }
    }

    public NumberFormat(String newLocale) {
        for (int i = 0; i < LOCNAMES.length; i++) {
            if (LOCNAMES[i].equals(newLocale)) {
                this.locale = newLocale;
                break;
            }
            if (i == (LOCNAMES.length - 1)) {
                throw new RuntimeException("Не удалось найти локализацию:'" + newLocale + "'");
            }
        }
    }

    private void checkWithRegExp(String line) {
        try {
            if (!line.isEmpty()) {
                if (line.substring(0, 1).equals("-")) {
                    minus = true;
                    results.add(endings[2][0]);
                } else {
                    minus = false;
                }
            }
            if (line.matches("^[-]?\\d{1,}$")) {
                floatFormat = false;
            } else if (line.matches("^[-]?\\d{1,}[.]\\d{1,}$")) {
                floatFormat = true;
            } else {
                throw new IllegalArgumentException("Не верный формат числа'" + line + "'");
            }
        } catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Не верный формат числа'" + line + "'\n" + e.toString());
        }
    }

    private String getEndingOfTheWord(String intermediateResult, int blocCurrent, int numberEnding, int numberLine, int isNegative) {
        String str = "";
        if (isNegative == 0) {
            //если блок меньше чем тысяча то окончание не требуется
            if (blocCurrent < 2) {
                str = "";
            }
            if (blocCurrent == 2) {
                str = endings[0][ENDING_LEN];
            } else if (blocCurrent > 2) {
                str = endings[1][ENDING_LEN];
            }
        } else {
            if (numberEnding > ENDING_LEN) {
                numberEnding = ENDING_LEN;
            }
            str = endings[numberLine][numberEnding];
        }
        if (!intermediateResult.isEmpty()) {
            results.add(intermediateResult + str);
        }
        return "";
    }

    private void getTriadName(String str, int numberBloc) {
        try {
            //текущий элемент  в блоке. 
            int currentIndex = str.length();
            lastThird = 0;
            if (currentIndex > 0) {
                for (int i = currentIndex; i > 0; i--) {
                    if (!str.substring(i - 1).equals("0")) {
                        if (numberBloc == 2) {
                            lastThird = Integer.valueOf(str.substring(i - 1, currentIndex));
                        } else {
                            lastThird = Integer.valueOf(str.substring(0));
                        }
                        break;
                    }
                }
            }
            while (currentIndex > 0) {
                //часть блока в числовом представлении, используется при нахождение окончании (тысяч, тысяча, тысчи)
                int partBlock = Integer.valueOf(str.substring(0, currentIndex));
                String intermediateResult = tokens[currentIndex - 1][Integer.valueOf(str.substring(0, 1))];
                str = str.substring(1);
                if ((currentIndex == 2) && (partBlock < 20) && (partBlock > 9)) {
                    intermediateResult = tokens[0][partBlock];
                    if (!intermediateResult.isEmpty()) {
                        results.add(intermediateResult);
                    }
                    intermediateResult = scaleUnits.get(numberBloc - 1);
                    getEndingOfTheWord(intermediateResult, numberBloc, partBlock, 0, 0);
                    str = str.substring(1);
                    currentIndex = 0;
                } else if (currentIndex == 1) {
                    if ((numberBloc == 2) && (partBlock < 3)) {
                        intermediateResult = tokens[3][partBlock];
                    } else {
                        partBlock = lastThird;
                    }
                    if (!intermediateResult.isEmpty()) {
                        results.add(intermediateResult);
                    }
                    intermediateResult = scaleUnits.get(numberBloc - 1);
                    getEndingOfTheWord(intermediateResult, numberBloc, partBlock, (numberBloc == 2) ? 0 : 1, 1);
                } else if (!intermediateResult.isEmpty()) {
                    if ((currentIndex == 3) && (!str.equals("00"))) {
                        results.add(intermediateResult);
                        results.add(endings[2][2]);
                    } else {
                        results.add(intermediateResult);
                    }
                }
                currentIndex--;
            }
        } catch (IndexOutOfBoundsException e) {
            throw new RuntimeException("Такой степени нет в справочнике.\n" + e.getMessage());
        }
    }

    private void convertInteger(String value, int isNegative) {
        lastThird = 0;
        int blocCount = (int) (Math.ceil(value.length() / 3.0));
        for (int blocCurrent = blocCount; blocCurrent > 0; blocCurrent--) {
            while ((value.indexOf("0") == 0)) {
                value = value.substring(1);
            }
            int currentIndex = (value.length() - (blocCurrent - 1) * 3);
            if (currentIndex > 0) {
                getTriadName((value.substring(0, currentIndex)), blocCurrent);
                value = value.substring(currentIndex);
            }
        }
        if (results.isEmpty()) {
            results.add(endings[2][3]);
        }
        if (isNegative == 1) {
            if ((locale.equals("ru")) && (results.get(results.size() - 1).equals(tokens[0][1]))) {
                results.set(results.size() - 1, tokens[3][1]);
            }
            if ((locale.equals("ru")) && (results.get(results.size() - 1).equals(tokens[0][2]))) {
                results.set(results.size() - 1, tokens[3][2]);
            }
        }
    }

    private void addPrefixInteger() {
        lastThird = Integer.valueOf(String.valueOf(lastThird).substring(String.valueOf(lastThird).length() - 1));
        if ((lastThird > 4) || (lastThird == 0)) {
            results.add(endings[2][1] + endings[3][2]);
        } else if (lastThird == 1) {
            results.add(endings[2][1] + endings[3][0]);
        } else {
            results.add(endings[2][1] + endings[3][1]);
        }
    }

    private void addPrefixFloat(int degreeOfNumber, int lastSize) {
        if (lastSize == results.size()) {
            results.add(endings[2][3]);
        }
        String str = "";
        if (degreeOfNumber == 1) {
            str = endings[5][0];
        } else if (degreeOfNumber == 2) {
            str = endings[5][1];
        } else {
            str = endings[4][((int) (degreeOfNumber % 3))] + scaleUnits.get((int) (degreeOfNumber / 3)) + "н";
        }
        if ((lastThird > 4) || (lastThird == 0)) {
            results.add(str + endings[3][2]);
        } else if (lastThird == 1) {
            results.add(str + endings[3][0]);
        } else {
            results.add(str + endings[3][1]);
        }
    }

    public void convertIntegerEN(String value) {
        int size = value.length();
        for (int i = 0; i < size; i++) {
            if (value.substring(i, i + 1).equals("0")) {
                results.add(tokens[3][0]);
            } else {
                results.add(tokens[0][Integer.valueOf(value.substring(i, i + 1))]);
            }
        }
    }

    private String getResult() {
        String result = "";
        int size = results.size() - 1;
        for (int i = 0; i < size; i++) {
            if (!(results.get(i)).equals("")) {
                result += results.get(i) + " ";
            }
        }
        result += results.get(size);
        results.clear();
        return result;
    }

    public String format(String number) throws IllegalArgumentException {
        checkWithRegExp(number);
        if (minus) {
            number = number.substring(1);
        }
        if (floatFormat == false) {
            convertInteger(number, 0);
        } else {
            int indexOfPoint = number.indexOf(".");
            convertInteger(number.substring(0, indexOfPoint), 1);
            addPrefixInteger();
            int size = results.size();
            switch (locale) {
                case "ru": {
                    convertInteger(number.substring(indexOfPoint + 1), 1);
                    addPrefixFloat(number.length() - (indexOfPoint + 1), size);
                    break;
                }
                case "en": {
                    convertIntegerEN(number.substring(indexOfPoint + 1));
                    break;
                }
            }
        }
        return getResult();
    }

}
