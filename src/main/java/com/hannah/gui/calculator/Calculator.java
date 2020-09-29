// Calculator.java
package com.hannah.gui.calculator;

public class Calculator {

	private static final char NO_OP = '\0';
	private static final char PLUS = '+';
	private static final char SUBTRACT = '-';
	private static final char MULTIPLY = '*';
	private static final char DIVIDE = '/';

	private float number1 = 0.0F;
	// private float number2 = Float.NaN;
	private char operator = NO_OP;

	// 等于运算
	public String opEquals(String number) {
		float result;

		if (operator == NO_OP) {
			result = parseNumber(number);
		} else {
			result = performOperation(parseNumber(number));
		}
		operator = NO_OP;

		number1 = result;

		return Float.toString(result);
	}

	// 加法运算
	public String opAdd(String number) {
		float result;

		if (operator == NO_OP) {
			result = parseNumber(number);
		} else {
			result = performOperation(parseNumber(number));
		}
		operator = PLUS;

		number1 = result;

		return Float.toString(result);
	}

	// 减法运算
	public String opSubtract(String number) {
		float result;

		if (operator == NO_OP) {
			result = parseNumber(number);
		} else {
			result = performOperation(parseNumber(number));
		}
		operator = SUBTRACT;

		number1 = result;

		return Float.toString(result);
	}

	// 乘法运算
	public String opMultiply(String number) {
		float result;

		if (operator == NO_OP) {
			result = parseNumber(number);
		} else {
			result = performOperation(parseNumber(number));
		}
		operator = MULTIPLY;

		number1 = result;

		return Float.toString(result);
	}

	// 除法运算
	public String opDivide(String number) {
		float result;

		if (operator == NO_OP) {
			result = parseNumber(number);
		} else {
			result = performOperation(parseNumber(number));
		}
		operator = DIVIDE;

		number1 = result;

		return Float.toString(result);
	}

	// 处理运算符
	private float performOperation(float number2) {
		float result = 0.0F;

		switch (operator) {
		case PLUS:
			result = number1 + number2;
			break;
		case SUBTRACT:
			result = number1 - number2;
			break;
		case MULTIPLY:
			result = number1 * number2;
			break;
		case DIVIDE:
			result = number1 / number2;
			break;
		}

		return result;
	}

	// 将字符串类型转换为浮点型
	private static float parseNumber(String number) {
		float real_number;

		try {
			real_number = Float.parseFloat(number);
		} catch (NumberFormatException e) {
			real_number = Float.NaN;
		}

		return real_number;
	}

}
