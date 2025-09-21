/* Quinn Keenan, 301504914, 19/09/2025
I am enforcing a singleton pattern on this class to prevent the misuse of my backend. Furthermore,
I may have to alter this class to support questions with multiple correct answers in future sprints. */

package Test;

import java.util.Random;
import TestQuestion.TestQuestion;
import TestQuestion.TestQuestionBuilder;
import TestQuestion.TestQuestionOption;

class Test
{
    private final String[] correctAnswerMessages =
	{
		"Excellent!",
		"Congratulations!",
		"Keep it up!",
		"You're on fire, champ!",
		"Sheesh!"
	};

	private static Test currentInstance;
	private byte currentQuestionIndex;
	private final Random gen;

	private final String[] incorrectAnswerMessages =
	{
		"Whoops! Brush it off, champ!",
		"Sorry, but that's not correct.",
		"You'll get 'em next time!",
		"Better luck next time!",
		"Wake up!"
	};

	private final TestQuestion[] questions;
    private byte score;
	private static final String TEST_INCOMPLETE_MESSAGE = "Your test has not been completed yet.";
	private static final String TEST_COMPLETED_MESSAGE = "Your test has been completed already.";

    private Test(byte questionCount)
    {
        int count;

		gen = new Random();
        questions = new TestQuestion[questionCount];

        for (count = 0; count <  questionCount; count++)
        {
            questions[count] = TestQuestionBuilder.build();
        }
    }

	public static Test createTest(byte questionCount)
	{
		if (currentInstance == null)
		{
			currentInstance = new Test(questionCount);
		}

		return currentInstance;
	}

	public byte getCurrentQuestionNumberOfOptions()
	{
		return (byte) questions[currentQuestionIndex].getOptions().length;
	}

	public String getCurrentQuestionCorrectOptionString()
	{
		TestQuestion currentQuestion = questions[currentQuestionIndex];

		for (TestQuestionOption option : currentQuestion.getOptions())
		{
			if (option.getIsCorrect())
			{
				return option.toString();
			}
		}

		return null;
	}

	public String getCurrentQuestionString()
	{
		StringBuilder sb = new StringBuilder();
		int count;
		TestQuestion currentQuestion;
		TestQuestionOption[] options;

		if (!isActive())
		{
			throw new IllegalStateException(TEST_COMPLETED_MESSAGE);
		}

		currentQuestion = questions[currentQuestionIndex];
		sb.append(String.format("%s\n\n", currentQuestion.getPrompt()));
		options = currentQuestion.getOptions();

		for (count = 0; count < options.length; count++)
		{
			sb.append(String.format("%d: %s\n", count + 1, options[count]));
		}

		sb.append("\n");
		return sb.toString();
	}

	public String getRandomAnswerMessage(boolean answerIsCorrect)
	{
		String[] answerMessages;

		if (answerIsCorrect)
		{
			answerMessages = correctAnswerMessages;
		}
		else
		{
			answerMessages = incorrectAnswerMessages;
		}

		return answerMessages[gen.nextInt(answerMessages.length)];
	}

	public String getScoreString()
	{
		if (isActive())
		{
			throw new IllegalStateException(TEST_INCOMPLETE_MESSAGE);
		}

		return String.format(
			"You scored %d/%d (%.2f%%).",
			score,
			questions.length,
			(float) score / questions.length * 100
		);
	}

	public boolean isActive()
	{
		return currentQuestionIndex < questions.length;
	}

	public void reset()
	{
		if (currentInstance.isActive())
		{
			throw new IllegalStateException(TEST_INCOMPLETE_MESSAGE);
		}

		currentInstance = null;
		currentQuestionIndex = 0;
		score = 0;
		TestQuestionBuilder.reset();
	}

	/**
	 * Increments the test's score if the answer is correct. Also increments currentQuestionIndex.
	 * @param answer The number associated with the TestQuestionOption displayed on the UI. Should start from 1.
	 * @return Indicates whether the answer is correct.
	 */
	public boolean submitAnswer(byte answer)
	{
		boolean isCorrect;

		if (!isActive())
		{
			throw  new IllegalStateException(TEST_COMPLETED_MESSAGE);
		}

		answer--; //Done because the displayed options are not 0-based, unlike all arrays stored in RAM.
		isCorrect = questions[currentQuestionIndex].getOptions()[answer].getIsCorrect();

		if (isCorrect)
		{
			score++;
		}

		currentQuestionIndex++;
		return isCorrect;
	}
}